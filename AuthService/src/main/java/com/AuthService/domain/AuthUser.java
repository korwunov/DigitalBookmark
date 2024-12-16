package com.AuthService.domain;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

//@NamedNativeQuery(
//        name = "get_all_users",
//        query =
//                "SELECT * FROM (" +
//                        "  SELECT id as id, name as name, username as username, password as password, filesid as filesID, role as role FROM teachers t " +
//                        "union" +
//                        "  SELECT id as id, name as name, username as username, password as password, filesid as filesID, role as role FROM students s " +
//                        ") as AuthUser",
//        resultSetMapping = "users_dto"
//)
//@Entity
//@Table(name = "teachers")
//@SqlResultSetMapping(
//        name = "users_dto",
//        classes = @ConstructorResult(
//                targetClass = AuthUser.class,
//                columns = {
//                        @ColumnResult(name = "id", type = Long.class),
//                        @ColumnResult(name = "name", type = String.class),
//                        @ColumnResult(name = "username", type = String.class),
//                        @ColumnResult(name = "password", type = String.class),
//                        @ColumnResult(name = "filesID", type = List.class),
//                        @ColumnResult(name = "role", type = EROLE.class)
//                }
//        )
//)
public class AuthUser extends AbstractUser {
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
