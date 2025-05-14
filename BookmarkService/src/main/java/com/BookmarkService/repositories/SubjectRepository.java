package com.BookmarkService.repositories;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Component
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);
    Optional<List<Subject>> findBySubjectTeachers(Teacher t);
    Optional<List<Subject>> findBySubjectStudents(Student s);
}
