package com.AuthService.controllers;

import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import com.AuthService.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public TokenDto login(@RequestBody SignInRequestDto request) {
        return authService.singIn(request);
    }

    /**
     * Пусть все могут регистрироваться, но потом только админ сможет подключить дисциплины к пользователям
     * @param request
     * @return TokenDto token
     */
    @PostMapping("/registration")
    public TokenDto register(@RequestBody SignUpRequestDto request) {
        return authService.signUp(request);
    }
}
