package com.Client.services;


import com.Client.model.UserSession;
import com.Client.model.request.UserEnabledRequest;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.model.response.TokenDTO;
import com.Client.model.request.UserLoginRequestDTO;
import com.Client.model.request.UserRegistrationRequestDTO;
import com.Client.model.response.UserDataDTO;
import com.Client.utils.HttpRequestEntity;
import com.vaadin.flow.component.UI;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
@Log4j
public class AuthService {
//    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String AUTH_SERVICE_ROUTE = "/api/auth";

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
            TokenDTO token = restTemplate.postForObject(this.authServiceUrl + AUTH_SERVICE_ROUTE + "/login", requestBody, TokenDTO.class);
            if (token == null) {
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте войти позже");
            }
            UserSession session = new UserSession();
            session.setAuthToken(token.token);
            UI.getCurrent().getSession().setAttribute("userSession", session);
            saveUserDataToSession();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /registration returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте войти позже");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(String username, String password, String fio, String role) {
        UserRegistrationRequestDTO requestBody = new UserRegistrationRequestDTO(username, password, fio, role);
        try {
            TokenDTO token = restTemplate.postForObject(this.authServiceUrl + AUTH_SERVICE_ROUTE + "/registration", requestBody, TokenDTO.class);
            if (token == null) {
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
            UserSession session = new UserSession();
            session.setAuthToken(token.token);
            UI.getCurrent().getSession().setAttribute("userSession", session);
            saveUserDataToSession();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /registration returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUserEnabled(Long userId, boolean targetEnabledValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<UserEnabledRequest> requestEntity = new HttpEntity<>(new UserEnabledRequest(userId, targetEnabledValue), headers);
        try {
            restTemplate.exchange(
                    this.authServiceUrl + AUTH_SERVICE_ROUTE + "/setEnabled",
                    HttpMethod.PUT,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /setEnabled returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте сменить признак активации позже");
            }
        }
    }

    private void saveUserDataToSession() throws ExecutionException, InterruptedException {
        HttpEntity<Void> requestEntity = HttpRequestEntity.getRequestEntity();
        try {
            ResponseEntity<UserDataDTO> response = restTemplate.exchange(
                    this.authServiceUrl + AUTH_SERVICE_ROUTE + "/getUserData",
                    HttpMethod.GET,
                    requestEntity,
                    UserDataDTO.class);
            UserSession userSession = (UserSession) UI.getCurrent().getSession().getAttribute("userSession");
            userSession.setUserData(response.getBody());
            UI.getCurrent().getSession().setAttribute("userSession", userSession);
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /registration returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте зарегистрироваться позже");
            }
        }
    }
}
