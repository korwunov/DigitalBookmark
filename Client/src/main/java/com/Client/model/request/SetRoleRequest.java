package com.Client.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetRoleRequest {
    public Long userId;
    public String role;
}
