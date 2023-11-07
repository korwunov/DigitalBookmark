package com.DigitalBookmark.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "marks")
public class SubjectMarkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long markRecordId;

    @Column(nullable = false)
    private int markValue;

    @ManyToOne(targetEntity = Subject.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subject markSubject;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student markOwner;

    @ManyToOne(targetEntity = Teacher.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Teacher markGiver;
}
