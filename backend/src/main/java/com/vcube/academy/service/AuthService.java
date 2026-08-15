package com.vcube.academy.service;

import com.vcube.academy.dto.auth.*;
import com.vcube.academy.dto.user.UserDto;
import com.vcube.academy.entity.RefreshToken;
import com.vcube.academy.entity.Role;
import com.vcube.academy.entity.RoleType;
import com.vcube.academy.entity.User;
import com.vcube.academy.exception.EmailAlreadyExistsException;
import com.vcube.academy.exception.InvalidTokenException;
import com.vcube.academy.exception.ResourceNotFoundException;
import com.vcube.academy.repository.RefreshTokenRepository;
import com.vcube.academy.repository.RoleRepository;
import com.vcube.academy.repository.UserRepository;
import com.vcube.academy.security.JwtTokenProvider;
import com.vcube.academy.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository         userRepository;
    private final RoleRepository         roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;
    private final AuthenticationManager  authenticationManager;
    private final JwtTokenProvider       jwtTokenProvider;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    // ─── Register ────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Role studentRole = roleRepository.findByName(RoleType.STUDENT)
            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleType.STUDENT));

        User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail().toLowerCase())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .roles(Set.of(studentRole))
            .build();

        user = userRepository.save(user);
        log.info("Registered new student: {}", user.getEmail());

        UserPrincipal principal = UserPrincipal.of(user);
        String accessToken  = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, principal, accessToken, refreshToken);
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail().toLowerCase(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findByEmailWithRoles(principal.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", principal.getEmail()));

        // Revoke old refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken  = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, principal, accessToken, refreshToken);
    }

    // ─── Refresh ─────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (stored.getRevoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }
        if (stored.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new InvalidTokenException("Refresh token has expired — please login again");
        }

        // Rotate: revoke old, issue new
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        UserPrincipal principal = UserPrincipal.of(user);
        String accessToken  = jwtTokenProvider.generateAccessToken(principal);
        String newRefresh   = createRefreshToken(user);

        return buildAuthResponse(user, principal, accessToken, newRefresh);
    }

    // ─── Logout ──────────────────────────────────────────────────────────────

    @Transactional
    public void logout(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("User logged out: {}", user.getEmail());
    }

    // ─── Current user ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findByEmailWithRoles(principal.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));
        return toDto(user);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
            .build();
        return refreshTokenRepository.save(token).getToken();
    }

    private AuthResponse buildAuthResponse(User user, UserPrincipal principal,
                                            String accessToken, String refreshToken) {
        Set<String> roles = principal.getAuthorities().stream()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .collect(Collectors.toSet());

        return AuthResponse.of(
            accessToken,
            refreshToken,
            jwtTokenProvider.getAccessTokenExpirationMs(),
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            roles
        );
    }

    private UserDto toDto(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(r -> r.getName().name())
            .collect(Collectors.toSet());
        return UserDto.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .isActive(user.getIsActive())
            .roles(roles)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
