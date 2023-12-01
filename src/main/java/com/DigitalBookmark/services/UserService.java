package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.EROLE;
import com.DigitalBookmark.domain.User;
import com.DigitalBookmark.repositories.UserRepository;
import com.DigitalBookmark.web.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

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
        Set<EROLE> roles = user.getRoles();
        if (roles.contains(roleInfo.getRole())) throw new Exception("role " + roleInfo.getRole() + " already at user");
        roles.add(roleInfo.getRole());
        user.setRoles(roles);
        userRepository.save(user);
        return user;
    }
}
