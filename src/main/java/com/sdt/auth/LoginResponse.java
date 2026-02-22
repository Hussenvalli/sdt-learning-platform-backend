package com.sdt.auth;

public record LoginResponse(
        boolean success,
        String message,
        String accessToken
) {
}
