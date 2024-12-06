package com.AuthService.services;

import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.AuthService.domain.AuthUser;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserAuthService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenDto signUp(SignUpRequestDto request) {
        AuthUser user = new AuthUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new TokenDto(jwt);
    }

    public TokenDto singIn(SignInRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());
        jwtService.generateToken(user);
        return new TokenDto(jwtService.generateToken(user));
    }

}
