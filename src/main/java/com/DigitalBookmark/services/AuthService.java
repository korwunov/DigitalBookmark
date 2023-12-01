package com.DigitalBookmark.services;

import com.DigitalBookmark.AuthService.domain.dto.AuthResponseDTO;
import com.DigitalBookmark.AuthService.domain.dto.LoginResponseDTO;
import com.DigitalBookmark.web.dto.LoginDTO;
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

    @Value("${rabbitmq.auth.routing_key.name}")
    private String authRoutingKey;

    @Value("${rabbitmq.login.routing_key.name}")
    private String loginRoutingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public AuthService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public LoginResponseDTO login(LoginDTO creds) {
        LoginResponseDTO resp = (LoginResponseDTO) rabbitTemplate.convertSendAndReceive(exchange, loginRoutingKey, creds);
        LOGGER.info(String.format("Login data sent: " + creds.toString()));
        LOGGER.info(String.format("Received data " + resp));
        return resp;
    }

    public AuthResponseDTO auth(String token) {
        AuthResponseDTO resp = (AuthResponseDTO) rabbitTemplate.convertSendAndReceive(exchange, authRoutingKey, token);
        LOGGER.info(String.format("Token sent: " + token));
        LOGGER.info(String.format("Received data " + resp));
        return resp;
    }

}
