package com.BookmarkService.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
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
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
