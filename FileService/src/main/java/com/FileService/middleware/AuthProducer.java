package com.FileService.middleware;

import com.BookmarkService.domain.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Data
@RequiredArgsConstructor
public class AuthProducer {
    private final RabbitTemplate template;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;    //Имя exchange pool

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKeyName;

    public <T extends User> User getUserData(String token) {
        return (User) this.template.convertSendAndReceive(exchangeName, authRoutingKeyName, token);
    }
}
