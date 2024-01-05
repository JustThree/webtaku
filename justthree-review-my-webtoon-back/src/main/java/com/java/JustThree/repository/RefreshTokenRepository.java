package com.java.JustThree.repository;

import com.java.JustThree.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUser_UsersEmail(String usersEmail);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByUser_UsersEmail(String usersEmail);
    void deleteByUser_UsersEmail(String usersEmail);
}
