package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User user = userService.createUser("newuser", "pass", Role.ROLE_USER);
        assertEquals("newuser", user.getUsername());
        assertEquals("encoded", user.getPassword());
        assertEquals(Role.ROLE_USER, user.getRole());
    }

    @Test
    void createUser_usernameExists() {
        when(userRepository.findByUsername("exists")).thenReturn(Optional.of(new User()));
        assertThrows(CustomException.class, () -> userService.createUser("exists", "pass", Role.ROLE_USER));
    }

    @Test
    void findByUsername_success() {
        User user = User.builder().username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("john");
        assertEquals("john", found.getUsername());
    }

    @Test
    void findByUsername_notFound() {
        when(userRepository.findByUsername("none")).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> userService.findByUsername("none"));
    }
}