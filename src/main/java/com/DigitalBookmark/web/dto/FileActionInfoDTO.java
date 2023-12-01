package com.DigitalBookmark.web.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class FileActionInfoDTO {
    private String action;
    private Long userId;
    private String fileId;
}
