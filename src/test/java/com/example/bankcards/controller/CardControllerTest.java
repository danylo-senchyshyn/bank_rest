package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CardControllerTest {

    @Mock
    private CardService cardService;
    @Mock
    private UserService userService;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private CardController cardController;

    public CardControllerTest() {
        openMocks(this);
    }

    @Test
    void testCreateCard() {
        CardCreateDto dto = new CardCreateDto("1234567890123456", "user", "12/28", BigDecimal.valueOf(1000));
        CardDto cardDto = new CardDto(1L, "****", "user", null, null, null);
        when(cardService.createCard(dto)).thenReturn(cardDto);

        CardDto result = cardController.createCard(dto);
        assertEquals(1L, result.getId());
        verify(cardService).createCard(dto);
    }

    @Test
    void testGetUserCards() {
        when(userDetails.getUsername()).thenReturn("user");
        User user = new User();
        user.setUsername("user");

        Page<CardDto> page = new PageImpl<>(List.of(new CardDto()));
        when(userService.findByUsername("user")).thenReturn(user);
        when(cardService.getUserCards(eq(user), any(Pageable.class))).thenReturn(page);

        Page<CardDto> result = cardController.getUserCards(userDetails, 0, 10);
        assertFalse(result.isEmpty());
    }

    @Test
    void testTransfer() {
        when(userDetails.getUsername()).thenReturn("user");
        User user = new User();
        user.setUsername("user");

        TransferDto dto = new TransferDto(1L, 2L, BigDecimal.valueOf(100));

        when(userService.findByUsername("user")).thenReturn(user);
        cardController.transfer(userDetails, dto);

        verify(cardService).transferBetweenCards(1L, 2L, BigDecimal.valueOf(100), user);
    }

    @Test
    void testGetBalance() {
        when(userDetails.getUsername()).thenReturn("user");
        User user = new User();
        user.setUsername("user");

        when(userService.findByUsername("user")).thenReturn(user);
        when(cardService.getBalance(1L, user)).thenReturn(BigDecimal.valueOf(500));

        BigDecimal balance = cardController.getBalance(userDetails, 1L);
        assertEquals(BigDecimal.valueOf(500), balance);
    }
}