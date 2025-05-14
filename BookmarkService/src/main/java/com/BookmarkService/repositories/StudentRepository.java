package com.BookmarkService.repositories;

import com.BookmarkService.domain.Group;
import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<List<Student>> findByGroupAndStudentSubjects(Group g, Subject s);
}