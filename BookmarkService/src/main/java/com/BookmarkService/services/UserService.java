package com.BookmarkService.services;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.User;
import com.BookmarkService.repositories.UserRepository;
import com.BookmarkService.web.dto.request.RoleDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User setRole(RoleDTO roleInfo) {
        Optional<User> userRecord = this.userRepository.findById(roleInfo.getId());
        if (userRecord.isEmpty()) throw new BadRequestException("user with id " + roleInfo.getId() + " not found");
        User user = userRecord.get();
        EROLE role = user.getRole();
        if (role.equals(roleInfo.getRole())) throw new BadRequestException("role " + roleInfo.getRole() + " already at user");
        user.setRole(roleInfo.getRole());
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
