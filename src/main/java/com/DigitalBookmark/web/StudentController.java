package com.DigitalBookmark.web;

import com.DigitalBookmark.AuthService.domain.dto.AuthResponseDTO;
import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.services.AuthService;
import com.DigitalBookmark.services.StudentService;
import com.DigitalBookmark.web.httpStatusesExceptions.BadRequestException;
import com.DigitalBookmark.web.httpStatusesExceptions.NotFoundException;
import com.DigitalBookmark.web.httpStatusesExceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/api/students")
@ResponseBody
public class StudentController {
    public StudentService studentService;

    public AuthService authService;

    @Autowired
    public StudentController(StudentService studentService, AuthService authService) {
        this.studentService = studentService;
        this.authService = authService;
    }

    @GetMapping
    public List<Student> getStudents() {//@RequestHeader("Authorization") String token) {
        //AuthResponseDTO authData = this.authService.auth(token);
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Optional<Student> getStudentById(@PathVariable Long id) {
        if (id == null) throw new BadRequestException("no id in requets");
        try {
            return studentService.getStudentByID(id);
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping
    public HttpStatus addStudent(@RequestBody Student u) {
        try {
            studentService.addStudent(u);
            return HttpStatus.CREATED;
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return HttpStatus.OK;
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
