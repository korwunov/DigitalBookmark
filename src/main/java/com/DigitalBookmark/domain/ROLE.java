package com.DigitalBookmark.domain;

import org.springframework.security.core.GrantedAuthority;

public enum ROLE implements GrantedAuthority {
    ROLE_STUDENT,
    ROLE_TEACHER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
