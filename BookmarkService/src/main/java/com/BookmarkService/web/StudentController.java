package com.BookmarkService.web;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Student;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.services.MarkService;
import com.BookmarkService.services.StudentService;
import com.BookmarkService.web.dto.request.StudentBySubjectAndGroup;
import com.BookmarkService.web.dto.response.MarkResponseDTO;
import com.BookmarkService.web.dto.response.StudentResponseDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;
import java.util.Optional;

@Controller   //Обозначение класса контроллера для Spring
@Slf4j        //Подключение логирования
@RequestMapping("/api/bookmark/students")    //Роутинг для запросов данного класса
@ResponseBody //Обозначает, что HTTP обработчики данного класса должны возращать тело ответа
public class StudentController {
    public StudentService studentService;
    public MarkService markService;

    @Autowired
    public StudentController(StudentService studentService, MarkService markService) {
        this.studentService = studentService;
        this.markService = markService;
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping
    public List<Student> getStudents(@RequestHeader("Authorization") String token, Object user) {//@RequestHeader("Authorization") String token) {
        //AuthResponseDTO authData = this.authService.auth(token);
        return studentService.getAllStudents();
    }
    //Аннотация определяет этот метод как обработчик
    //HTTP запроса GET с ID в переменной в пути
    @Authentication(roles = {EROLE.ROLE_TEACHER, EROLE.ROLE_STUDENT})
    @GetMapping("/{id}")
    public Optional<Student> getStudentById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
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

    @Authentication(roles = {EROLE.ROLE_STUDENT})
    @GetMapping("/marks")
    public List<MarkResponseDTO> getStudentMarks(@RequestHeader("Authorization") String token, Object user) {
        try {
            Student s = (Student) user;
            return markService.getStudentMarks(s);
        } catch (ClassCastException ex) {
            throw new BadRequestException("Unable to cast user to Student");
        }
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER})
    @GetMapping("/getStudentsBySubjectAndGroup")
    public List<StudentResponseDTO> getStudentsBySubjectAndGroup(@RequestHeader("Authorization") String token, Object user,
                                                                 @RequestParam Long groupId, @RequestParam Long subjectId) {
        return this.studentService.getStudentsBySubjectAndGroupIds(groupId, subjectId);
    }


//    @PostMapping
//    public HttpStatus addStudent(@RequestBody Student u) {
//        try {
//            studentService.addStudent(u);
//            return HttpStatus.CREATED;
//        }
//        catch (Exception e) {
//            throw new BadRequestException(e.getMessage());
//        }
//    }

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public HttpStatus deleteStudent(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return HttpStatus.OK;
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
