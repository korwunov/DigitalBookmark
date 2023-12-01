package com.DigitalBookmark.web.dto;

import com.DigitalBookmark.domain.EROLE;
import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private EROLE role;
}
