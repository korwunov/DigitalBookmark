package com.BookmarkService.middleware;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthContext {
    private final RabbitTemplate template;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;    //Имя exchange pool

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKeyName;

    @Value("${rabbitmq.auth.queue.name}")
    private String authQueueName;

    @Around("@annotation(com.BookmarkService.middleware.Authentication)")
    public Object getUserData(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authentication.class);
        EROLE[] requiredRoles = auth.roles();
        System.out.println(Arrays.toString(requiredRoles));

        String token = (String) Arrays.stream(joinPoint.getArgs()).toList().get(0);
        User user = (User) this.template.convertSendAndReceive(exchangeName, authRoutingKeyName, token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (user.getRole() == EROLE.ROLE_ADMIN) {
            joinPoint.getArgs()[1] = user;
            return joinPoint.proceed(joinPoint.getArgs());
        }
        if (!Arrays.asList(requiredRoles).contains(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with role " + user.getRole() + " is not allowed to go here");
        }
        joinPoint.getArgs()[1] = user;
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
