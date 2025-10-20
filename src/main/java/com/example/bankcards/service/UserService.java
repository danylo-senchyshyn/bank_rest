package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type User service.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Find by username user.
     *
     * @param username the username
     * @return the user
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    /**
     * Create user user.
     *
     * @param username the username
     * @param password the password
     * @param role     the role
     * @return the user
     */
    public User createUser(String username, String password, Role role) {
        if(userRepository.findByUsername(username).isPresent()) {
            throw new CustomException("A user with this username already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .active(true)
                .build();

        System.err.println("Creating user: " + user.getUsername() + " with role: " + user.getRole());

        return userRepository.save(user);
    }

    /**
     * Delete all users.
     */
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }


    /**
     * Gets all users.
     *
     * @return the all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}