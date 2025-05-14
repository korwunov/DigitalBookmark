package com.Client.services;

import com.Client.model.UserSession;
import com.Client.model.request.MarkDTO;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.model.response.MarksDataDTO;
import com.Client.utils.HttpRequestEntity;
import lombok.extern.java.Log;
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

import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class MarksService {
    @Value("${bookmark-service.url}")
    private String bookmarkServiceUrl;

    private static final String BOOKMARK_SERVICE_ROUTE = "/api/bookmark";

    private final RestTemplate restTemplate;

    @Autowired
    public MarksService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    public List<MarksDataDTO> getMarks(String pathPart) {
        HttpEntity<Void> requestEntity = HttpRequestEntity.getRequestEntity();

        try {
            return restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/" + pathPart + "/marks",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<MarksDataDTO>>() {}
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
                log.error(String.format("POST /registration returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте получить оценки позже");
            }
        }
    }

    public void setMark(MarkDTO request) throws RestClientException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", UserSession.getUserToken());
            HttpEntity<MarkDTO> httpEntity = new HttpEntity<>(request, headers);
            restTemplate.exchange(
                    this.bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/teachers/setMark",
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
                log.error(String.format("POST /setMark returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте сохранить оценку позже");
            }
        }
    }
}
