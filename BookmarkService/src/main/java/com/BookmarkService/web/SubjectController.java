package com.BookmarkService.web;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.web.dto.SubjectDTO;
import com.BookmarkService.services.SubjectService;
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

    public SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @PostMapping
    public Subject addSubject(@RequestBody SubjectDTO s) {
        try {
            return this.subjectService.addSubject(s);
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping
    public List<Subject> getAllSubjects() {
        return this.subjectService.getAllSubjects();
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Long id) {
        try {
            return this.subjectService.getSubjectById(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public Subject deleteSubjectById(@PathVariable Long id) {
        try {
            return this.subjectService.deleteSubject(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
