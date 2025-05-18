package com.Client.model.response;

import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class SubjectDTO {
    public Long id;
    public String name;
    public List<TeacherDTO> teachers;
    public List<StudentDTO> students;

    public List<String> getTeachersNames() {
        return Objects.nonNull(this.teachers) ? this.teachers.stream().map(TeacherDTO::getName).collect(Collectors.toList()) : List.of("Отсутствуют подключенные преподаватели");
    }

    public List<String> getStudentsNames() {
        return Objects.nonNull(this.students) ? this.students.stream().map(StudentDTO::getName).collect(Collectors.toList()) : List.of("Отсутствуют подключенные студенты");
    }
}
