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
import java.util.List;


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

    @Around("@annotation(com.FileService.middleware.Authentication)")
    public Object getUserData(ProceedingJoinPoint joinPoint) throws Throwable {
        String token = (String) Arrays.stream(joinPoint.getArgs()).toList().getFirst();
        User user = (User) this.template.convertSendAndReceive(exchangeName, authRoutingKeyName, token);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "пошел нахуй чмо");
        }
        joinPoint.getArgs()[1] = user;
        System.out.println("345");
        return joinPoint.proceed(joinPoint.getArgs());
    }
}
