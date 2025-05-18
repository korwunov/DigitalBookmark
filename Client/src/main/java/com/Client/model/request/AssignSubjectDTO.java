package com.Client.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignSubjectDTO {
    public Long userId;
    public List<Long> subjectIds;
}
