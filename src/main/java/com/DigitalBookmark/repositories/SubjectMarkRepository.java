package com.DigitalBookmark.repositories;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.SubjectMarkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component
public interface SubjectMarkRepository extends JpaRepository<SubjectMarkRecord, Long> {
}
