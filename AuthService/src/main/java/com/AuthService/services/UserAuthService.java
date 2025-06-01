package com.AuthService.services;

import com.AuthService.domain.AuthUser;
import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.domain.User;
import com.AuthService.repositories.StudentRepository;
import com.AuthService.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserAuthService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    public void create(AuthUser user) {
        if (
                studentRepository.findByUsername(user.getUsername()).isPresent() ||
                teacherRepository.findByUsername(user.getUsername()).isPresent()
        ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь с таким логином уже существует");
        }
        if (user.getRole() == EROLE.ROLE_STUDENT) {
            Student s = new Student();
            s.setName(user.getName());
            s.setUsername(user.getUsername());
            s.setPassword(user.getPassword());
            s.setRole(user.getRole());
            s.setEnabled(true);
            studentRepository.save(s);
        }
        if (user.getRole() == EROLE.ROLE_TEACHER) {
            Teacher t = new Teacher();
            t.setName(user.getName());
            t.setUsername(user.getUsername());
            t.setPassword(user.getPassword());
            t.setRole(user.getRole());
            t.setEnabled(true);
            teacherRepository.save(t);
        }

    }

    public <T extends User> User getByUsername(String username) {
        Optional<Student> studentRecord = studentRepository.findByUsername(username);
        Optional<Teacher> teacherRecord = teacherRepository.findByUsername(username);
        if (studentRecord.isEmpty() && teacherRecord.isEmpty()) {
            return null;
        }
        if (studentRecord.isPresent() && teacherRecord.isEmpty()) {
            return studentRecord.get();
        }
        if (studentRecord.isEmpty() && teacherRecord.isPresent()) {
            return teacherRecord.get();
        }
        return null;
        //throw new Exception("Multiple users with same usernames " + studentRecord.get() + teacherRecord.get());
    }

    public <T extends User> User getById(Long id) {
        Optional<Student> studentRecord = studentRepository.findById(id);
        Optional<Teacher> teacherRecord = teacherRepository.findById(id);
        if (studentRecord.isEmpty() && teacherRecord.isEmpty()) {
            return null;
        }
        if (studentRecord.isPresent() && teacherRecord.isEmpty()) {
            return studentRecord.get();
        }
        if (studentRecord.isEmpty() && teacherRecord.isPresent()) {
            return teacherRecord.get();
        }
        return null;
    }

    public void setEnabled(Long id, boolean isEnabled) {
        Optional<Student> studentRecord = studentRepository.findById(id);
        Optional<Teacher> teacherRecord = teacherRepository.findById(id);
        if (studentRecord.isPresent() && teacherRecord.isEmpty()) {
            Student stud = studentRecord.get();
            stud.setEnabled(isEnabled);
            studentRepository.save(stud);
        }
        if (studentRecord.isEmpty() && teacherRecord.isPresent()) {
            Teacher teac = teacherRecord.get();
            teac.setEnabled(isEnabled);
            teacherRepository.save(teac);
        }
    }

//    @SneakyThrows
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() throws Exception {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }
}
