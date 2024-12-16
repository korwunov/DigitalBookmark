package com.AuthService.repositories;

import com.AuthService.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Component
@Repository
public interface AuthSubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);
}
