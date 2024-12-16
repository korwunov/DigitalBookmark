package com.BookmarkService.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "students")
@DiscriminatorValue("ROLE_STUDENT")
public class Student extends User{


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "markOwner")
    private List<SubjectMarkRecord> marksList;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "subjectStudents")
    private List<Subject> studentSubjects;
}
