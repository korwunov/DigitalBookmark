package com.DigitalBookmark.web;

import com.DigitalBookmark.AuthService.domain.dto.LoginResponseDTO;
import com.DigitalBookmark.web.dto.LoginDTO;
import com.DigitalBookmark.services.AuthService;
import com.DigitalBookmark.web.httpStatusesExceptions.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

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
        LoginResponseDTO resp = this.authService.login(creditionals);
        if (!Objects.equals(resp.getStatus(), "OK")) throw new UnauthorizedException("Bad creditionals");
        return resp.getToken();
    }
}
