package com.Client.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignGroupRequest {
    public Long studentId;
    public Long groupId;
}
