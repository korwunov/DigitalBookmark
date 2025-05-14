package com.Client.model.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarksDataDTO {
    public Long markId;
    public String subjectName;
    public String giverName;
    public String ownerName;
    public String ownerGroup;
    public LocalDate markDate;
    public int markValue;
}
