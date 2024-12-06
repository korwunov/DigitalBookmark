package com.BookmarkService.web.dto;


import lombok.Data;

import java.util.List;
@Data
public class SubjectDTO {
    private String name;
    private List<Long> teachersIds;
}
