package com.AuthService.services;

import com.BookmarkService.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQService {
    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @RabbitListener(queues = "digitalbookmark_auth_queue")
    public <T extends User> User authUserRequest(String token) {
        if (token.isBlank()) return null;
        try {
            String tokenWithoutType = new StringBuilder(token).substring(token.indexOf(" ") + 1, token.length());
            String username = jwtService.extractUserName(tokenWithoutType);
            System.out.println("username from token " + username);
            User user = userAuthService.getByUsername(username);
            if (user == null) { return null; }
            System.out.println(user);
            return user;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
