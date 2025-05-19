package com.BookmarkService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "role")
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "users_seq", allocationSize = 1)
    @JsonView(Views.UserDataResponse.class)
    private Long id;

    @Column(nullable = false)
    @JsonView(Views.UserDataResponse.class)
    private String name;

    @Column(name = "username", nullable = false)
    @JsonView(Views.UserDataResponse.class)
    private String username;

    @JsonView(Views.UserDataResponse.class)
    @Column(name = "is_enabled", nullable = false)
    protected boolean enabled;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = true)
    private List<Long> filesID;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JsonView(Views.UserDataResponse.class)
    private EROLE role;
}
