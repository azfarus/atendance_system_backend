package com.example.atendance_system_backend.forgotpassword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenTableRepository extends JpaRepository<TokenTable , Long> {
    Optional<TokenTable> findTokenTableByToken(String token);
}
