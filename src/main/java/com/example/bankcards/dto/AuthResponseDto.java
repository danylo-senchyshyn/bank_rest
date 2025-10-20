package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Auth response dto.
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
}