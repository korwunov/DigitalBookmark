package com.AuthService.services;

import com.AuthService.dto.UserDataResponse;
import com.AuthService.dto.SignInRequestDto;
import com.AuthService.dto.SignUpRequestDto;
import com.AuthService.dto.TokenDto;
import com.BookmarkService.domain.*;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.AuthService.domain.AuthUser;

@Service
@RequiredArgsConstructor
@Log4j
public class AuthenticationService {
    private final UserAuthService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public ObjectMapper mapper;

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

    public <T extends User> UserDataResponse getUserDataByToken(String token) {
        if (token.isBlank()) return null;
        try {
            String tokenWithoutType = new StringBuilder(token).substring(token.indexOf(" ") + 1, token.length());
            String username = jwtService.extractUserName(tokenWithoutType);
            log.info("Extracted username from token " + username);
            User user = userService.getByUsername(username);
            if (user == null) {
                log.info("User with username: " + username + " not found");
                return null;
            }
            log.info("Found user with id: " + user);
//            isFieldInClass(user, "marksList") ? user
            ObjectWriter viewWriter = mapper.writerWithView(Views.UserDataResponse.class);
            String json = viewWriter.writeValueAsString(user);
            return mapper.readValue(json, UserDataResponse.class);
        }
        catch (Exception e) {
            log.error("Exception occurred during user login " + e.getMessage());
            return null;
        }
    }

    public TokenDto singIn(SignInRequestDto request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(token);

        var user = userService.userDetailsService().loadUserByUsername(request.getUsername());
        jwtService.generateToken(user);
        return new TokenDto(jwtService.generateToken(user));
    }
//
//    private boolean isFieldInClass(Object object, String fieldName) {
//        return Arrays.stream(object.getClass().getFields())
//                .anyMatch(f -> f.getName().equals(fieldName));
//    }

}
