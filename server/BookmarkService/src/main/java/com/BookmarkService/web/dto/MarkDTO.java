package com.BookmarkService.web.dto;

import lombok.Data;

@Data
public class MarkDTO {
    private Long studentId;
    private Long subjectId;
    private int markValue;
}
