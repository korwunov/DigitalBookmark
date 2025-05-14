package com.BookmarkService.web.dto.response;

import com.BookmarkService.domain.Group;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO {
    public Long id;
    public String name;
    public Group group;
}
