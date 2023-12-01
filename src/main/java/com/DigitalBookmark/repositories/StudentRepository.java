package com.DigitalBookmark.repositories;

import com.DigitalBookmark.domain.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@Component
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String email);
}