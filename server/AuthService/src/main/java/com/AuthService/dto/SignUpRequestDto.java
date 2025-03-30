package com.AuthService.dto;

import com.BookmarkService.domain.EROLE;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequestDto {
    private String username;
    private String password;
    private String name;
    private EROLE role;
}
