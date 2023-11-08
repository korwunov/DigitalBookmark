package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.dto.LoginDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing_key.name}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public AuthService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void login(LoginDTO creds) {
        rabbitTemplate.convertSendAndReceive(exchange, routingKey, creds);
        LOGGER.info(String.format("Login data sent: " + creds.toString()));
    }

}
