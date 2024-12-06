package com.AuthService.dto;

import com.BookmarkService.domain.EROLE;
import lombok.Data;

@Data
public class SignUpRequestDto {
    private String username;
    private String password;
    private String name;
    private EROLE role;
}
