package com.DigitalBookmark.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teachers")
public class Teacher extends User {

    @OneToMany(mappedBy = "markGiver")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<SubjectMarkRecord> givenMarks;

    @ManyToMany(mappedBy = "subjectTeachers")
    private List<Subject> teacherSubjects;

    @Override
    public String toString() {
        return this.getId().toString();
    }

}
