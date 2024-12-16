package com.BookmarkService.web;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.SubjectMarkRecord;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.web.dto.MarkDTO;
import com.BookmarkService.services.MarkService;
import com.BookmarkService.services.TeacherService;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
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

    @Authentication(roles = {EROLE.ROLE_STUDENT, EROLE.ROLE_TEACHER})
    @GetMapping
    public List<Teacher> getAllTeachers(@RequestHeader("Authorization") String token, Object user) {
        return this.teacherService.getAllTeachers();
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER})
    @GetMapping("/{id}")
    public Teacher getTeacherById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        return this.teacherService.getTeacherById(id);
    }

//    @PostMapping
//    public HttpStatus createTeacher(@RequestBody Teacher t) throws Exception {
//        try {
//            this.teacherService.addTeacher(t);
//            return HttpStatus.CREATED;
//        }
//        catch (Exception e) {
//            throw new BadRequestException(e.getMessage());
//        }
//    }

    @Authentication(roles = {EROLE.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public Teacher deleteTeacherById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        try {
            return this.teacherService.deleteTeacherById(id);
        }
        catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Authentication(roles = {EROLE.ROLE_TEACHER})
    @PostMapping("/setMark")
    public SubjectMarkRecord addMark(@RequestHeader("Authorization") String token, Object user, @RequestBody MarkDTO mark) {
        try {
            return this.markService.addMarkRecord(mark);
        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
