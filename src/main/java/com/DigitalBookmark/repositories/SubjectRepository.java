package com.DigitalBookmark.repositories;

import com.DigitalBookmark.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
