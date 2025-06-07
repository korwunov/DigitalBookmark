package com.Client.services;

import com.Client.model.UserSession;
import com.Client.model.request.FileManageRequest;
import com.Client.model.response.FileDTO;
import com.Client.model.response.HttpErrorResponseDTO;
import com.Client.utils.HttpRequestEntity;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Log4j
public class FilesService {
    @Value("${file-service.url}")
    private String fileServiceUrl;

    private static final String FILE_SERVICE_ROUTE = "/api/files";

    private final RestTemplate restTemplate;

    @Autowired
    public FilesService(RestTemplate restTemplate) {this.restTemplate = restTemplate;}

    public List<FileDTO> getAllUsersFiles(boolean shared) {
        try {
            return restTemplate.exchange(
                    this.fileServiceUrl + FILE_SERVICE_ROUTE + (shared ? "/shared" : ""),
                    HttpMethod.GET,
                    HttpRequestEntity.getRequestEntity(),
                    new ParameterizedTypeReference<List<FileDTO>>() {}
            ).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            } else {
                log.error(String.format("GET /files returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте запросить список файлов позже");
            }
        }
    }

    public Resource downloadFile(Long fileId) {
        RestTemplate specialRestTemplate = new RestTemplate();
        specialRestTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        try {
            return specialRestTemplate.exchange(
                    this.fileServiceUrl + FILE_SERVICE_ROUTE + "/download/" + fileId,
                    HttpMethod.GET,
                    HttpRequestEntity.getRequestEntity(),
                    Resource.class
            ).getBody();
        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            if (httpException.getClass().getSuperclass() == HttpClientErrorException.class) {
                throw new RestClientException(
                        Objects.requireNonNull(
                                httpException.getResponseBodyAs(HttpErrorResponseDTO.class)
                        ).message
                );
            } else {
                log.error(String.format("GET /download returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте запросить список файлов позже");
            }
        }
    }

    public void uploadFile(byte[] fileData, String fileName) {
        RestTemplate specialRestTemplate = new RestTemplate();
        specialRestTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", UserSession.getUserToken());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        try {
            specialRestTemplate.exchange(
                    this.fileServiceUrl + FILE_SERVICE_ROUTE + "/upload",
                    HttpMethod.POST,
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
                log.error(String.format("POST /upload returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте загрузить файл позже");
            }
        }
    }

    public void shareFile(Long fileId, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", UserSession.getUserToken());
        HttpEntity<FileManageRequest> requestEntity = new HttpEntity<>(new FileManageRequest(fileId, userId), headers);
        try {
            restTemplate.exchange(
                    this.fileServiceUrl + FILE_SERVICE_ROUTE + "/shared",
                    HttpMethod.POST,
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
                log.error(String.format("POST /shared returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте предоставить доступ позже");
            }
        }
    }

    public void deleteFile(Long id) {
        try {
            restTemplate.exchange(
                    this.fileServiceUrl + FILE_SERVICE_ROUTE + "/delete/" + id,
                    HttpMethod.DELETE,
                    HttpRequestEntity.getRequestEntity(),
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
                log.error(String.format("DELETE /delete returned %s, response body %s", httpException.getStatusCode(), httpException.getResponseBodyAs(HttpErrorResponseDTO.class)));
                throw new RestClientException("Наблюдаются технические проблемы, попробуйте удалить файл позже");
            }
        }
    }
}
