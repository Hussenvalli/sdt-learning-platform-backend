package com.sdt.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserAccountRepository userAccountRepository, JwtTokenService jwtTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        UserAccountEntity existingUser = userAccountRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!PasswordHasher.matches(request.password(), existingUser.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return buildSuccessResponse(existingUser, false, "Login successful");
    }

    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim();
        if (userAccountRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UserAccountEntity userAccount = new UserAccountEntity();
        userAccount.setId(UUID.randomUUID().toString());
        userAccount.setFullName(request.fullName().trim());
        userAccount.setEmail(normalizedEmail);
        userAccount.setPasswordHash(PasswordHasher.hash(request.password()));
        userAccount.setCreatedAt(Instant.now());

        userAccountRepository.save(userAccount);

        return new RegisterResponse(
                true,
                "Account created successfully",
                userAccount.getId(),
                userAccount.getFullName(),
                userAccount.getEmail()
        );
    }

    private LoginResponse buildSuccessResponse(UserAccountEntity userAccount, boolean isNewUser, String message) {
        String token = jwtTokenService.generateToken(toUserAccount(userAccount));
        return new LoginResponse(
                true,
                message,
                token,
                userAccount.getId(),
                userAccount.getFullName(),
                isNewUser
        );
    }

    private UserAccount toUserAccount(UserAccountEntity entity) {
        return new UserAccount(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt()
        );
    }
}
