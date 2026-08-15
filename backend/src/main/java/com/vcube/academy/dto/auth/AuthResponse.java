package com.vcube.academy.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long   expiresIn;
    private Long   userId;
    private String fullName;
    private String email;
    private Set<String> roles;

    public static AuthResponse of(String accessToken, String refreshToken,
                                   Long expiresIn, Long userId,
                                   String fullName, String email,
                                   Set<String> roles) {
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(expiresIn)
            .userId(userId)
            .fullName(fullName)
            .email(email)
            .roles(roles)
            .build();
    }
}
