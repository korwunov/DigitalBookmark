package com.BookmarkService.repositories;

import com.BookmarkService.domain.SubjectMarkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface SubjectMarkRepository extends JpaRepository<SubjectMarkRecord, Long> {
    Optional<List<SubjectMarkRecord>> findByMarkSubject(Subject s);
}
