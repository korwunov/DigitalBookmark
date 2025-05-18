package com.BookmarkService.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponseDTO {
    public Long id;
    public String name;
    public List<TeacherResponseDTO> teachers;
    public List<StudentResponseDTO> students;
}
