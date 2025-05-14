package com.BookmarkService.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarkResponseDTO {
    public Long markId;
    public String subjectName;
    public String giverName;
    public String ownerName;
    public String ownerGroup;
    public LocalDate markDate;
    public int markValue;
}
