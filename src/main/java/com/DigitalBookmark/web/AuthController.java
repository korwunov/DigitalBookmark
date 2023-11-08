package com.DigitalBookmark.web;

import com.DigitalBookmark.domain.dto.LoginDTO;
import com.DigitalBookmark.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api")
public class AuthController {

    public AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PutMapping("/login")
    public String login(@RequestBody LoginDTO creditionals) {
        this.authService.login(creditionals);
        return "message send";
    }
}
