package com.BookmarkService.web.dto.request;

import lombok.Data;

@Data
public class MarkDTO {
    private Long studentId;
    private Long subjectId;
    private int markValue;
}
