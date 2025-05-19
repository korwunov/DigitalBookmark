package com.Client.model.response;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class UserDataDTO {
    public Long id;
    public String name;
    public String username;
    public boolean enabled;
    public List<Long> filesID;
    public String role;
    public List<UserSubjectDTO> studentSubjects;
    public List<UserSubjectDTO> teachersSubjects;
    public GroupDTO group;

    public String getRoleString() {
        return switch (this.role) {
            case "ROLE_STUDENT" -> "Студент";
            case "ROLE_TEACHER" -> "Преподаватель";
            case "ROLE_ADMIN" -> "Администратор";
            default -> "";
        };
    }
}

class UserSubjectDTO {
    public Long id;
    public String name;
}
