package com.BookmarkService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Group.class)
    private Group group;

    @Override
    public String toString() {
        return this.getId().toString();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
