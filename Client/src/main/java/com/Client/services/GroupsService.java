package com.Client.services;

import com.Client.model.response.GroupDTO;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.utils.HttpRequestEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
public class GroupsService {
    @Value("${bookmark-service.url}")
    private String bookmarkServiceUrl;

    private static final String BOOKMARK_SERVICE_ROUTE = "/api/bookmark";

    private final RestTemplate restTemplate;

    @Autowired
    public GroupsService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    public List<GroupDTO> getAllGroups() {
        try {
            return restTemplate.exchange(
                    bookmarkServiceUrl + BOOKMARK_SERVICE_ROUTE + "/groups",
                    HttpMethod.GET,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<List<GroupDTO>>() {}
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
                log.error(String.format("POST /api/bookmark/groups returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте поставить оценку позже");
            }
        }
    }
}
