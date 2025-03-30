package com.BookmarkService.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubjectsToAddDTO {
    private Long userId;
    private List<Long> subjectIds;
}
