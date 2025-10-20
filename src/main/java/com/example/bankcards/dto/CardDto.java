package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The type Card dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {
    private Long id;
    private String number;
    private String ownerUsername;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
}