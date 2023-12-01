package com.DigitalBookmark.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubjectsToAddDTO {
    private Long teacherId;
    private List<Long> ids;
}
