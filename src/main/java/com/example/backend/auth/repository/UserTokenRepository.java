package com.example.backend.auth.repository;

import com.example.backend.auth.entity.UserToken;
import com.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenHashAndAndTokenType(String tokenHash, String tokenType);

    Optional<UserToken> deleteUserTokenByUserAndAndTokenType(User user, String tokenType);
}
