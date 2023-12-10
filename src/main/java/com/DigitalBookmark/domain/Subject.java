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
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "markSubject")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<SubjectMarkRecord> subjectMarks;

    @ManyToMany(targetEntity = Teacher.class)
    @JsonIgnore
    private List<Teacher> subjectTeachers;

    @ManyToMany
    @JsonIgnore
    private List<Student> subjectStudents;
}
