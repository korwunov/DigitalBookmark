package com.BookmarkService.web.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SubjectsToAddDTO {
    private Long userId;
    private List<Long> subjectIds;
}
