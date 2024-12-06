package com.BookmarkService.web;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.domain.User;
import com.BookmarkService.services.StudentService;
import com.BookmarkService.services.UserService;
import com.BookmarkService.web.dto.RoleDTO;
import com.BookmarkService.web.dto.SubjectsToAddDTO;
import com.BookmarkService.services.TeacherService;
import com.BookmarkService.services.MarkService;
import com.BookmarkService.domain.SubjectMarkRecord;
import com.BookmarkService.web.httpStatusesExceptions.ForbiddenException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
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
