package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.SubjectMarkRecord;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.domain.dto.MarkDTO;
import com.DigitalBookmark.repositories.SubjectMarkRepository;
import com.DigitalBookmark.services.MarkService;
import com.DigitalBookmark.services.TeacherService;
import com.DigitalBookmark.web.utils.BadRequestException;
import com.DigitalBookmark.web.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/teachers")
@ResponseBody
public class TeacherController {
    public TeacherService teacherService;

    public MarkService markService;

    @Autowired
    public TeacherController(TeacherService teacherService, MarkService markService) {
        this.teacherService = teacherService;
        this.markService = markService;
    }

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return this.teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public Teacher getTeacherById(@PathVariable Long id) {
        return this.teacherService.getTeacherById(id);
    }

    @PostMapping
    public HttpStatus createTeacher(@RequestBody Teacher t) throws Exception {
        try {
            this.teacherService.addTeacher(t);
            return HttpStatus.CREATED;
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/setMark/{id}")
    public SubjectMarkRecord addMark(@PathVariable Long id, @RequestBody MarkDTO mark) {
        try {
            return this.markService.addMarkRecord(id, mark);
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
