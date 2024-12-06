package com.BookmarkService.repositories;

import com.BookmarkService.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select user from User user where username = ?1")   //1й user как * в sql, User - класс, который ожидаем увидеть, 2й user - объект этого класса
    Optional<User> findByUsername(String username);
}
