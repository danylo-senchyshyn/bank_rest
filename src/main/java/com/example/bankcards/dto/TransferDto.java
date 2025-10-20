package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * The type Transfer dto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDto {
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
}