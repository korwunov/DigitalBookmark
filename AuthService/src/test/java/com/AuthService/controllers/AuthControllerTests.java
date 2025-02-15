package com.AuthService.controllers;

import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import com.AuthService.repositories.StudentRepository;
import com.AuthService.services.JwtService;
import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.AuthService.repositories.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthControllerTests {
    @Autowired
    public AuthController authController;

    @Autowired
    public JwtService jwtService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void registerTeacher() {
        final String USERNAME = "teacher@mail.ru";
        SignUpRequestDto requestDto = new SignUpRequestDto(USERNAME, "1234", "Teacher", EROLE.ROLE_TEACHER);
        this.authController.register(requestDto);
        Teacher t = teacherRepository.findByUsername(USERNAME).get();
        assertEquals(requestDto.getUsername(), t.getUsername());
        assertEquals(requestDto.getName(), t.getName());
        assertEquals(requestDto.getRole(), t.getRole());
        teacherRepository.delete(t);
    }

    @Test
    public void registerStudent() {
        final String USERNAME = "student@mail.ru";
        SignUpRequestDto requestDto = new SignUpRequestDto(USERNAME, "1234", "Student", EROLE.ROLE_STUDENT);
        this.authController.register(requestDto);
        Student s = studentRepository.findByUsername(USERNAME).get();
        assertEquals(requestDto.getUsername(), s.getUsername());
        assertEquals(requestDto.getName(), s.getName());
        assertEquals(requestDto.getRole(), s.getRole());
        studentRepository.delete(s);
    }

    @Test
    public void loginTeacher() {
        final String USERNAME = "teacher@mail.ru";
        final String PASSWORD = "1234";
        SignUpRequestDto registerRequestDto = new SignUpRequestDto(USERNAME, PASSWORD, "Teacher", EROLE.ROLE_TEACHER);
        this.authController.register(registerRequestDto);
        SignInRequestDto loginRequestDto = new SignInRequestDto(USERNAME, PASSWORD);
        TokenDto tokenDto = this.authController.login(loginRequestDto);
        String actualUsername = this.jwtService.extractUserName(tokenDto.token);
        assertEquals(USERNAME, actualUsername);
        teacherRepository.delete(
                teacherRepository.findByUsername(USERNAME).get()
        );
    }

//    @Test
//    public void loginStudent() {
//        final String USERNAME = "student@mail.ru";
//        final String PASSWORD = "1234";
//        SignUpRequestDto registerRequestDto = new SignUpRequestDto(USERNAME, PASSWORD, "Student", EROLE.ROLE_STUDENT);
//        this.authController.register(registerRequestDto);
//        SignInRequestDto loginRequestDto = new SignInRequestDto(USERNAME, PASSWORD);
//        TokenDto tokenDto = this.authController.login(loginRequestDto);
//        String actualUsername = this.jwtService.extractUserName(tokenDto.token);
//        assertEquals(USERNAME, actualUsername);
//        studentRepository.delete(
//                studentRepository.findByUsername(USERNAME).get()
//        );
//    }
}
