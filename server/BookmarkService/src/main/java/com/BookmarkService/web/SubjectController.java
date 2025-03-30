package com.BookmarkService.web;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.services.StudentService;
import com.BookmarkService.services.TeacherService;
import com.BookmarkService.web.dto.SubjectDTO;
import com.BookmarkService.services.SubjectService;
import com.BookmarkService.web.dto.SubjectsToAddDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/subjects")
@ResponseBody
public class SubjectController {
    @Autowired
    public SubjectService subjectService;
    @Autowired
    public TeacherService teacherService;
    @Autowired
    public StudentService studentService;

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @PostMapping
    public Subject addSubject(@RequestHeader("Authorization") String token, Object user, @RequestBody SubjectDTO s) {
        try {
            return this.subjectService.addSubject(s);
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping
    public List<Subject> getAllSubjects(@RequestHeader("Authorization") String token, Object user) {
        return this.subjectService.getAllSubjects();
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping("/{id}")
    public Subject getSubjectById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        try {
            return this.subjectService.getSubjectById(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/assignSubjectsForTeacher")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public Teacher addSubjectsForTeacher(@RequestHeader("Authorization") String token, Object user, @RequestBody SubjectsToAddDTO subjectsInfo) {
        return this.subjectService.addSubjectsToTeacher(subjectsInfo);
    }

    @PutMapping("/assignSubjectsForStudent")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public Student addSubjectForStudent(@RequestHeader("Authorization") String token, Object user, @RequestBody SubjectsToAddDTO subjectsInfo) {
        return this.subjectService.addSubjectToStudent(subjectsInfo);
    }

    @PutMapping("/unassignSubjectsForTeacher")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public Teacher removeSubjectsForTeacher(@RequestHeader("Authorization") String token, Object user, @RequestBody SubjectsToAddDTO subjectsInfo) {
        return this.subjectService.unassignSubjectToTeacher(subjectsInfo);
    }

    @PutMapping("/unassignSubjectsForStudent")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public Student removeSubjectForStudent(@RequestHeader("Authorization") String token, Object user, @RequestBody SubjectsToAddDTO subjectsInfo) {
        return this.subjectService.unassignSubjectToStudent(subjectsInfo);
    }

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public Subject deleteSubjectById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        try {
            return this.subjectService.deleteSubject(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
