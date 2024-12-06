package com.BookmarkService.services;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.User;
import com.BookmarkService.repositories.UserRepository;
import com.BookmarkService.web.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User setRole(RoleDTO roleInfo) throws Exception {
        Optional<User> userRecord = this.userRepository.findById(roleInfo.getId());
        if (userRecord.isEmpty()) throw new Exception("user with id " + roleInfo.getId() + " not found");
        User user = userRecord.get();
        EROLE role = user.getRole();
        if (role.equals(roleInfo.getRole())) throw new Exception("role " + roleInfo.getRole() + " already at user");
        user.setRole(roleInfo.getRole());
        userRepository.save(user);
        return user;
    }
}
