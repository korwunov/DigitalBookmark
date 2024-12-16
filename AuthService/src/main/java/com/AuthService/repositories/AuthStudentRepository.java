package com.AuthService.repositories;

import com.AuthService.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface AuthStudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
}