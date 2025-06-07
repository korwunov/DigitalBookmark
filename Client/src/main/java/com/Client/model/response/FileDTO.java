package com.Client.model.response;

import lombok.Data;

@Data
public class FileDTO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private Long fileOwner;
}
