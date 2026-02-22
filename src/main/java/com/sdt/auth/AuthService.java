package com.sdt.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final InMemoryUserStore userStore;
    private final JwtTokenService jwtTokenService;

    public AuthService(InMemoryUserStore userStore, JwtTokenService jwtTokenService) {
        this.userStore = userStore;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        return userStore.findByEmail(request.email())
                .map(existingUser -> loginExistingUser(request, existingUser))
                .orElseGet(() -> registerAndLoginNewUser(request));
    }

    private LoginResponse loginExistingUser(LoginRequest request, UserAccount existingUser) {
        if (!PasswordHasher.matches(request.password(), existingUser.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return buildSuccessResponse(existingUser, false, "Login successful");
    }

    private LoginResponse registerAndLoginNewUser(LoginRequest request) {
        UserAccount userAccount = new UserAccount(
                UUID.randomUUID().toString(),
                resolveFullName(request),
                request.email().trim(),
                PasswordHasher.hash(request.password()),
                Instant.now()
        );
        userStore.save(userAccount);

        return buildSuccessResponse(userAccount, true, "Account created and login successful");
    }

    private LoginResponse buildSuccessResponse(UserAccount userAccount, boolean isNewUser, String message) {
        String token = jwtTokenService.generateToken(userAccount);
        return new LoginResponse(
                true,
                message,
                token,
                userAccount.id(),
                userAccount.fullName(),
                isNewUser
        );
    }

    private String resolveFullName(LoginRequest request) {
        if (request.fullName() != null && !request.fullName().isBlank()) {
            return request.fullName().trim();
        }

        String email = request.email().trim();
        int separatorIndex = email.indexOf('@');
        if (separatorIndex > 0) {
            return email.substring(0, separatorIndex);
        }
        return email;
    }
}
