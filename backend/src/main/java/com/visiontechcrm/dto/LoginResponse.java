package com.visiontechcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** Successful login payload: JWT plus a few user fields the SPA needs. */
@Data
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private long expiresInMs;
}
