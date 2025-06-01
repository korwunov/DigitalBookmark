package com.AuthService.controllers;

import com.AuthService.dto.*;
import com.AuthService.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public TokenDto login(@RequestBody SignInRequestDto request) {
        return authService.singIn(request);
    }

    /**
     * Пусть все могут регистрироваться, но потом только админ сможет подключить дисциплины к пользователям
     * @param request - DTO с данными пользователя
     * @return TokenDto token
     */
    @PostMapping("/registration")
    public TokenDto register(@RequestBody SignUpRequestDto request) {
        return authService.signUp(request);
    }

    @GetMapping("/getUserData")
    public UserDataResponse getUserData(@RequestHeader("Authorization") String token) {return authService.getUserDataByToken(token);}

    @PutMapping("/setEnabled")
    public void setUserEnabled(@RequestHeader("Authorization") String token, @RequestBody UserStatusRequest request) {
        if (token.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is required");
        authService.setEnabled(token, request);
    }
}
