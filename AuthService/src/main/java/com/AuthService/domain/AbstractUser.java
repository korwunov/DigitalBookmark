package com.AuthService.domain;

import com.BookmarkService.domain.EROLE;
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
public abstract class AbstractUser implements UserDetails {
    @Id
    @SequenceGenerator(name = "SEQ", sequenceName = "CUSTOM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOM_SEQ")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(nullable = true)
    private List<Long> filesID;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EROLE role;
}
