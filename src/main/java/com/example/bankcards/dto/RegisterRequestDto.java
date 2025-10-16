package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String password;
    private Role role;
}