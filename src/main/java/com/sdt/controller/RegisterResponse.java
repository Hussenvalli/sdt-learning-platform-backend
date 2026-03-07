package com.sdt.controller;

public record RegisterResponse(
        boolean success,
        String message,
        String userId,
        String fullName,
        String email
) {
}
