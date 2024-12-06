package com.BookmarkService.web.dto;

import com.BookmarkService.domain.EROLE;
import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private EROLE role;
}
