package com.DigitalBookmark.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubjectsToAddDTO {
    private Long userId;
    private List<Long> subjectIds;
}
