package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequestDto;
import com.example.bankcards.dto.AuthResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AuthControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    public AuthControllerTest() {
        openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        AuthRequestDto dto = new AuthRequestDto("user", "pass", Role.ROLE_USER);
        User user = new User();
        user.setUsername("user");
        user.setRole(Role.ROLE_USER);

        when(userService.createUser("user", "pass", Role.ROLE_USER)).thenReturn(user);
        when(jwtUtil.generateToken("user", "ROLE_USER")).thenReturn("token");

        ResponseEntity<AuthResponseDto> response = authController.register(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    void testRegisterThrowsException() {
        AuthRequestDto dto = new AuthRequestDto("user", "pass", Role.ROLE_USER);
        when(userService.createUser(any(), any(), any())).thenThrow(new CustomException("Ошибка"));

        ResponseEntity<AuthResponseDto> response = authController.register(dto);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testLoginSuccess() {
        AuthRequestDto dto = new AuthRequestDto("user", "pass", Role.ROLE_USER);
        User user = new User();
        user.setUsername("user");
        user.setRole(Role.ROLE_USER);

        when(userService.findByUsername("user")).thenReturn(user);
        when(jwtUtil.generateToken("user", "ROLE_USER")).thenReturn("jwtToken");

        AuthResponseDto result = authController.login(dto);

        assertEquals("jwtToken", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginInvalidCredentials() {
        AuthRequestDto dto = new AuthRequestDto("user", "wrong", Role.ROLE_USER);

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authController.login(dto));
    }
}