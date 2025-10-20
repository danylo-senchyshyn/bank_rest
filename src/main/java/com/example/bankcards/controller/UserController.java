package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Gets all users.
     *
     * @return the all users
     */
    @GetMapping("/get/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Create user user.
     *
     * @param username the username
     * @param password the password
     * @param role     the role
     * @return the user
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String role) {
        return userService.createUser(username, password, role.equalsIgnoreCase("ADMIN") ?
                Role.ROLE_ADMIN : Role.ROLE_USER);
    }

    /**
     * Delete all users response entity.
     *
     * @return the response entity
     */
    @PostMapping("/delete/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok("All users have been deleted");
    }
}