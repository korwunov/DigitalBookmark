package com.Client.services;

import com.Client.model.UserSession;
import com.Client.model.request.AssignSubjectDTO;
import com.Client.model.request.CreateSubjectDTO;
import com.Client.model.request.MarkDTO;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.model.response.SubjectDTO;
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
public class SubjectsService {
    @Value("${bookmark-service.url}")
    private String bookmarkServiceUrl;

    private static final String BOOKMARK_SERVICE_ROUTE = "/api/bookmark";

    private final RestTemplate restTemplate;

    @Autowired
    public SubjectsService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    public void createSubject(CreateSubjectDTO request) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<CreateSubjectDTO> httpEntity = new HttpEntity<>(request, headers);
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/subjects",
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<>() {}
            ).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /subjects returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте сохранить предмет позже");
            }
        }
    }

    public List<SubjectDTO> getAllUserSubjects() {
        try {
            return restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/subjects",
                    HttpMethod.GET,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<List<SubjectDTO>>() {}
            ).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("POST /subjects returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте получить предметы позже");
            }
        }
    }

    public void deleteSubject(Long id) {
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/subjects/" + id,
                    HttpMethod.DELETE,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<>() {}
            ).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            }
            else {
                log.error(String.format("DELETE /subjects returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте удалить предмет позже");
            }
        }
    }

    public void assignSubject(String role, AssignSubjectDTO request, String action) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<AssignSubjectDTO> httpEntity = new HttpEntity<>(request, headers);
        try {
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/subjects/" + action + "SubjectsFor" + ("ROLE_STUDENT".equals(role) ? "Student" : "Teacher"),
                    HttpMethod.PUT,
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
            }
            else {
                log.error(String.format("DELETE /subjects returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте подключить предмет позже");
            }
        }
    }
}
