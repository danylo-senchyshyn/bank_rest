package com.example.bankcards.controller;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    public UserControllerTest() {
        openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(new User()));
        List<User> users = userController.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    void testCreateUser_Admin() {
        User user = new User();
        user.setUsername("admin");
        when(userService.createUser("admin", "123", Role.ROLE_ADMIN)).thenReturn(user);

        User result = userController.createUser("admin", "123", "ADMIN");
        assertEquals("admin", result.getUsername());
    }

    @Test
    void testDeleteAllUsers() {
        doNothing().when(userService).deleteAllUsers();
        ResponseEntity<String> response = userController.deleteAllUsers();

        assertEquals("Все пользователи удалены", response.getBody());
        verify(userService).deleteAllUsers();
    }
}