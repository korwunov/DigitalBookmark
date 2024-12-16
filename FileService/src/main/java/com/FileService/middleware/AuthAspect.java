package com.FileService.middleware;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.BookmarkService.domain.User;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final RabbitTemplate template;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;    //Имя exchange pool

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKeyName;

    @Value("${rabbitmq.auth.queue.name}")
    private String authQueueName;

    @Around("@annotation(com.FileService.middleware.Authentication)")
    public Object getUserData(ProceedingJoinPoint joinPoint) throws Throwable {
        String token = (String) Arrays.stream(joinPoint.getArgs()).toList().get(0);
        User user = (User) this.template.convertSendAndReceive(exchangeName, authRoutingKeyName, token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        joinPoint.getArgs()[1] = user;
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
