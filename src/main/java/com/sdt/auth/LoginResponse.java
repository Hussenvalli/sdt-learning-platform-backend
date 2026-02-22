package com.sdt.auth;

public record LoginResponse(
        boolean success,
        String message,
        String accessToken,
        String userId,
        String fullName,
        boolean newUser
) {
}
