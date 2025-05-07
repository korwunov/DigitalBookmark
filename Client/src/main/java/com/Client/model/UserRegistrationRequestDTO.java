package com.Client.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRegistrationRequestDTO {
    public String username;
    public String password;
    public String name;
    public String role;
}
