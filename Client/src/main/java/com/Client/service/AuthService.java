package com.Client.service;


import com.Client.model.HttpErrorResponseDTO;
import com.Client.model.TokenDTO;
import com.Client.model.UserLoginRequestDTO;
import com.Client.model.UserRegistrationRequestDTO;
import com.Client.utils.LocalStorage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.WebStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth-service.url}")
    private String authServiceUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void login(String username, String password) {
        UserLoginRequestDTO requestBody = new UserLoginRequestDTO(username, password);
        try {
            TokenDTO token = restTemplate.postForObject(this.authServiceUrl + "/login", requestBody, TokenDTO.class);
            if (token == null) {
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
            WebStorage.setItem("authToken", token.token);
            // TODO put role to localStorage
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error("POST /registration returned {}, response body {}", httpException.getStatusCode(),  httpException.getResponseBodyAs(HttpErrorResponseDTO.class));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
        }
    }

    public void register(String username, String password, String fio, String role) {
        UserRegistrationRequestDTO requestBody = new UserRegistrationRequestDTO(username, password, fio, role);
        try {
            TokenDTO token = restTemplate.postForObject(this.authServiceUrl + "/registration", requestBody, TokenDTO.class);
            // TODO put token and role to localStorage
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error("POST /registration returned {}, response body {}", httpException.getStatusCode(),  httpException.getResponseBodyAs(HttpErrorResponseDTO.class));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
        }


    }
}
