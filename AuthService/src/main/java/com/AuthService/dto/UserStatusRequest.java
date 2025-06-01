package com.AuthService.dto;

import lombok.Data;

@Data
public class UserStatusRequest {
    public Long userId;
    public boolean isEnabled;
}
