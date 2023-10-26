package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.services.StudentService;
import com.DigitalBookmark.web.utils.BadRequestException;
import com.DigitalBookmark.web.utils.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/api/student")
@ResponseBody
public class StudentController {
    public StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getUsers() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Optional<Student> getUserById(@PathVariable Long id) {
        if (id == null) throw new BadRequestException();
        try {
            return studentService.getStudentByID(id);
        }
        catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @PostMapping
    public HttpStatus addUser(@RequestBody Student u) {
        try {
            studentService.addStudent(u);
            return HttpStatus.CREATED;
        }
        catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteUser(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return HttpStatus.OK;
        }
        catch (Exception e) {
            throw new NotFoundException();
        }
    }
}
