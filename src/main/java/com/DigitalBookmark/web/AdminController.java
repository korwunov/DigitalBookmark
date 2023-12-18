package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.domain.SubjectMarkRecord;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.domain.User;
import com.DigitalBookmark.services.MarkService;
import com.DigitalBookmark.services.StudentService;
import com.DigitalBookmark.services.UserService;
import com.DigitalBookmark.web.dto.RoleDTO;
import com.DigitalBookmark.web.dto.SubjectsToAddDTO;
import com.DigitalBookmark.services.TeacherService;
import com.DigitalBookmark.web.httpStatusesExceptions.BadRequestException;
import com.DigitalBookmark.web.httpStatusesExceptions.ForbiddenException;
import com.DigitalBookmark.web.httpStatusesExceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/admin")
@ResponseBody
public class AdminController {
    public TeacherService teacherService;

    public StudentService studentService;

    public UserService userService;


    public MarkService markService;

    @Autowired
    public AdminController(TeacherService teacherService, StudentService studentService, UserService userService, MarkService markService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.userService = userService;
        this.markService = markService;
    }

    @PutMapping("/addSubjectsForTeacher")
    public Teacher addSubjectsForTeacher(@RequestBody SubjectsToAddDTO subjectsInfo) {
        try {
            return this.teacherService.addSubjectsToTeacher(subjectsInfo);
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PutMapping("/addSubjectForStudent")
    public Student addSubjectForStudent(@RequestBody SubjectsToAddDTO subjectsInfo) {
        try {
            return this.studentService.addSubjectToStudent(subjectsInfo);
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PutMapping("/setRole")
    public User setRole(@RequestBody RoleDTO roleInfo) {
        try {
            return this.userService.setRole(roleInfo);
        }
        catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }
    }

    @GetMapping("/getMarksStat")
    public List<SubjectMarkRecord> getMarksBySubjectAndDates(
            @RequestParam Long id,
            @RequestParam String dateFrom,
            @RequestParam String dateTo
    ) {
        return this.markService.getMarksBySubjectAndDates(id, dateFrom, dateTo);
    }
}
