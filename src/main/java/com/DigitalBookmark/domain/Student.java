package com.DigitalBookmark.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
public class Student extends User{


    @OneToMany(mappedBy = "markOwner")
    private List<SubjectMarkRecord> marksList;

    @ManyToMany(mappedBy = "subjectStudents")
    private List<Subject> studentSubjects;

    @Override
    public String toString() {
        return this.getId().toString();
    }

}
