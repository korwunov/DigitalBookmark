package com.BookmarkService.web;

import com.BookmarkService.domain.*;
import com.BookmarkService.middleware.Authentication;
import com.BookmarkService.services.UserService;
import com.BookmarkService.web.dto.request.RoleDTO;
import com.BookmarkService.services.MarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/bookmark/admin")
@ResponseBody
public class AdminController {

    public UserService userService;

    public MarkService markService;

    @Autowired
    public AdminController(UserService userService, MarkService markService) {
        this.userService = userService;
        this.markService = markService;
    }

    @PutMapping("/setRole")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public User setRole(@RequestHeader("Authorization") String token, Object user, @RequestBody RoleDTO roleInfo) {
        return this.userService.setRole(roleInfo);

    }

    @GetMapping("/users")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public List<User> getAllUsers(@RequestHeader("Authorization") String token, Object user) {
        return this.userService.getAllUsers();
    }

    @GetMapping("/getMarksStat")
    @Authentication(roles = {EROLE.ROLE_ADMIN})
    public List<SubjectMarkRecord> getMarksBySubjectAndDates(
            @RequestHeader("Authorization") String token,
            Object user,
            @RequestParam Long id,
            @RequestParam String dateFrom,
            @RequestParam String dateTo
    ) {
        return this.markService.getMarksBySubjectAndDates(id, dateFrom, dateTo);
    }
}
