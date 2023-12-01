package com.DigitalBookmark.AuthService.domain.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String status;
    private String token;
}
