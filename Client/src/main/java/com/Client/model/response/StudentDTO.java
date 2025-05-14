package com.Client.model.response;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String name;
    private GroupDTO group;
}
