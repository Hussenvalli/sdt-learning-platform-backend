package com.sdt.repo;

import com.sdt.common.UserAccount;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserStore {

    private final Map<String, UserAccount> usersByEmail = new ConcurrentHashMap<>();

    public Optional<UserAccount> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(normalizeEmail(email)));
    }

    public UserAccount save(UserAccount userAccount) {
        usersByEmail.put(normalizeEmail(userAccount.email()), userAccount);
        return userAccount;
    }

    void clear() {
        usersByEmail.clear();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
