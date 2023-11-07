package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.domain.dto.SubjectsToAddDTO;
import com.DigitalBookmark.services.TeacherService;
import com.DigitalBookmark.web.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/admin")
@ResponseBody
public class AdminController {
    public TeacherService teacherService;

    @Autowired
    public AdminController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PutMapping("/addSubjectsForTeacher/{id}")
    public Teacher addSubjectsForTeacher(@PathVariable Long id, @RequestBody SubjectsToAddDTO ids) {
        try {
            return this.teacherService.addSubjectsToTeacher(id, ids.getIds());
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
