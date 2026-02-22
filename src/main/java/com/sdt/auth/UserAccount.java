package com.sdt.auth;

import java.time.Instant;

public record UserAccount(
        String id,
        String fullName,
        String email,
        String passwordHash,
        Instant createdAt
) {
}
