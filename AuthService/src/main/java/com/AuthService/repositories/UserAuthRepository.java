package com.AuthService.repositories;

import com.AuthService.domain.AuthUser;
import com.BookmarkService.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long> {
    @Query("select user from User user where username = ?1")
    Optional<AuthUser> findByUsername(String username);
}