package com.BookmarkService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teachers")
@DiscriminatorValue("ROLE_TEACHER")
public class Teacher extends User {

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "markGiver")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<SubjectMarkRecord> givenMarks;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "subjectTeachers")
    private List<Subject> teacherSubjects;

    @Override
    public String toString() {
        return this.getId().toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
