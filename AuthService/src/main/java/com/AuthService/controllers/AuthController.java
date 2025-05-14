package com.AuthService.controllers;

import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import com.AuthService.services.AuthenticationService;
import com.AuthService.dto.UserDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}
