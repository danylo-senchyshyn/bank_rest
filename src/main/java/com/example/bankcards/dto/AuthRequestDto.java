package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {
    private String username;
    private String password;
    private Role role;
}