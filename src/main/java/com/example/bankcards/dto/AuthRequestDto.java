package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.*;

/**
 * The type Auth request dto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {
    private String username;
    private String password;
    private Role role;
}