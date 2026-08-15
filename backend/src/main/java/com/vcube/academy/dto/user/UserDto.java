package com.vcube.academy.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long      id;
    private String    fullName;
    private String    email;
    private String    phone;
    private Boolean   isActive;
    private Set<String> roles;
    private Instant   createdAt;
    private Instant   updatedAt;
}
