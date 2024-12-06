package com.BookmarkService.web;

import com.BookmarkService.domain.Student;
import com.BookmarkService.services.StudentService;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller   //Обозначение класса контроллера для Spring
@Slf4j        //Подключение логирования
@RequestMapping("/api/students")    //Роутинг для запросов данного класса
@ResponseBody //Обозначает, что HTTP обработчики данного класса должны возращать тело ответа
public class StudentController {
    public StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getStudents() {//@RequestHeader("Authorization") String token) {
        //AuthResponseDTO authData = this.authService.auth(token);
        return studentService.getAllStudents();
    }
    //Аннотация определяет этот метод как обработчик
    //HTTP запроса GET с ID в переменной в пути
    @GetMapping("/{id}")
    public Optional<Student> getStudentById(@PathVariable Long id) {
        //Если ID из пути запроса не определен, то вернуть 400
        if (id == null) throw new BadRequestException("no id in requets");
        try {
            //Поиск и возврат информации о студенте по ID
            return studentService.getStudentByID(id);
        }
        catch (Exception e) {
            //Если не удалось получиьт студента вернуть 404
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
