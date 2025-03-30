package com.BookmarkService.web.dto;

import lombok.Data;

@Data
public class FileActionInfoDTO {
    private String action;
    private Long userId;
    private String fileId;
}
