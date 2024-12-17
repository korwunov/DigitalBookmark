package com.AuthService.services;

import com.BookmarkService.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@RequiredArgsConstructor
@Log4j
public class RabbitMQService {
    private final JwtService jwtService;
    private final UserAuthService userAuthService;

    @RabbitListener(queues = "digitalbookmark_auth_queue")
    public <T extends User> User authUserRequest(String token) {
        if (token.isBlank()) return null;
        try {
            String tokenWithoutType = new StringBuilder(token).substring(token.indexOf(" ") + 1, token.length());
            String username = jwtService.extractUserName(tokenWithoutType);
            log.info("Extracted username from token " + username);
            User user = userAuthService.getByUsername(username);
            if (user == null) {
                log.info("User with username: " + username + " not found");
                return null;
            }
            log.info("Found user with id: " + user);
            return user;
        }
        catch (Exception e) {
            log.error("Exception occurred during user login " + e.getMessage());
            return null;
        }
    }
}
