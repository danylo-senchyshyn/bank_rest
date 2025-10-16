package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDto {
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
}