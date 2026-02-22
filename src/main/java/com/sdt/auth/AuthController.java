package com.sdt.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!isValidCredentials(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid email or password", null));
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "Login successful",
                        "demo-access-token"
                )
        );
    }

    private boolean isValidCredentials(LoginRequest request) {
        return "you@example.com".equalsIgnoreCase(request.email())
                && "password123".equals(request.password());
    }
}
