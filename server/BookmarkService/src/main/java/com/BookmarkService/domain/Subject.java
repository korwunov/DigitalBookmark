package com.BookmarkService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_generator")
    @SequenceGenerator(name = "subject_generator", sequenceName = "subjects_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "markSubject")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<SubjectMarkRecord> subjectMarks;

    @ManyToMany(targetEntity = Teacher.class)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JsonIgnore
    @Nullable
    private List<Teacher> subjectTeachers;

    @ManyToMany(targetEntity = Student.class)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JsonIgnore
    @Nullable
    private List<Student> subjectStudents;
}
