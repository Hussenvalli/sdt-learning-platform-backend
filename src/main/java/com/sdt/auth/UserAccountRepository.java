package com.sdt.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, String> {
    Optional<UserAccountEntity> findByEmailIgnoreCase(String email);
}
