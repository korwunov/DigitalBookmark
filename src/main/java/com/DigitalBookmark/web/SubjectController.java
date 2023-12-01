package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.web.dto.SubjectDTO;
import com.DigitalBookmark.services.SubjectService;
import com.DigitalBookmark.web.httpStatusesExceptions.BadRequestException;
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

    @PostMapping
    public Subject addSubject(@RequestBody SubjectDTO s) {
        try {
            return this.subjectService.addSubject(s);
        }
        catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public List<Subject> getAllSubjects() {
        return this.subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Long id) {
        try {
            return this.subjectService.getSubjectById(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

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
