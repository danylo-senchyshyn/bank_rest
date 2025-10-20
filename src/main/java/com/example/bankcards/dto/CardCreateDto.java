package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The type Card create dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreateDto {
    private String number;
    private String ownerUsername;
    private String validTill;
    private BigDecimal balance;
}