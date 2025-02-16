package com.AuthService.services;

import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import com.BookmarkService.domain.EROLE;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
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
        if (request.getRole() != EROLE.ROLE_TEACHER && request.getRole() != EROLE.ROLE_STUDENT) {
            throw new BadRequestException(String.format("Bad role name %s in request", request.getRole()));
        }
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
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(token);

        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());
        jwtService.generateToken(user);
        return new TokenDto(jwtService.generateToken(user));
    }

}
