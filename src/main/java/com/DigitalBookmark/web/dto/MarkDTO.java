package com.DigitalBookmark.domain.dto;

import lombok.Data;

@Data
public class MarkDTO {
    private Long subjectId;
    private Long markGiverId;
    private int markValue;
}
