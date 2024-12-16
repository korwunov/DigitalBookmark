package com.AuthService.repositories;

import com.AuthService.domain.Subject;
import com.AuthService.domain.SubjectMarkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface AuthSubjectMarkRepository extends JpaRepository<SubjectMarkRecord, Long> {
    Optional<List<SubjectMarkRecord>> findByMarkSubject(Subject s);
}
