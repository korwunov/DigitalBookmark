package com.DigitalBookmark.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDTO {
    public String username;
    public String password;
}
