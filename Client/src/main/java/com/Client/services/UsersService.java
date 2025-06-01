package com.Client.services;

import com.Client.model.UserSession;
import com.Client.model.request.AssignGroupRequest;
import com.Client.model.request.SetRoleRequest;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.model.response.UserDataDTO;
import com.Client.utils.HttpRequestEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class UsersService {
    @Value("${bookmark-service.url}")
    private String bookmarkServiceUrl;

    private static final String BOOKMARK_SERVICE_ROUTE = "/api/bookmark";

    private final RestTemplate restTemplate;

    @Autowired
    public UsersService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    public List<UserDataDTO> getAllUsers() {
        try {
            List<UserDataDTO> tmp = restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/admin/users",
                    HttpMethod.GET,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<List<UserDataDTO>>() {}
            ).getBody();
            return tmp;
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            } else {
                log.error(String.format("GET /teachers returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте получить список позже");
            }
        }
    }

    public void assignGroupToStudent(Long userId, Long groupId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<AssignGroupRequest> httpEntity = new HttpEntity<>(new AssignGroupRequest(userId, groupId), headers);
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/group/assign",
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {}
            );
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            } else {
                log.error(String.format("GET /group/assign returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте назначить группу позже");
            }
        }
    }

    public void unassignGroupFromStudent(Long userId) {
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/group/unassign/" + userId,
                    HttpMethod.POST,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            } else {
                log.error(String.format("POST /group/unassign returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте отключить группу позже");
            }
        }
    }

    public void setRole(Long userId, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<SetRoleRequest> requestEntity = new HttpEntity<>(new SetRoleRequest(userId, role), headers);
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/admin/setRole",
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
            } else {
                log.error(String.format("PUT /admin/setRole returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте сменить роль позже");
            }
        }
    }
}
