package com.sdt.auth;

public record RegisterResponse(
        boolean success,
        String message,
        String userId,
        String fullName,
        String email
) {
}
