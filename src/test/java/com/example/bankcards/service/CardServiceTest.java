package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CardService cardService;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).username("user").role(Role.ROLE_USER).build();
    }

    @Test
    void createCard_success() {
        CardCreateDto dto = CardCreateDto.builder()
                .ownerUsername("user")
                .number("1234567890123456")
                .validTill("12/28")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumber("1234567890123456")).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        var cardDto = cardService.createCard(dto);
        assertEquals("user", cardDto.getOwnerUsername());
        assertEquals("ACTIVE", cardDto.getStatus());
    }

    @Test
    void createCard_userNotFound() {
        CardCreateDto dto = CardCreateDto.builder()
                .ownerUsername("unknown").number("1234").validTill("12/28").build();

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> cardService.createCard(dto));
    }

    @Test
    void createCard_duplicateNumber() {
        CardCreateDto dto = CardCreateDto.builder()
                .ownerUsername("user").number("1234").validTill("12/28").build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumber("1234")).thenReturn(true);

        assertThrows(CustomException.class, () -> cardService.createCard(dto));
    }

    @Test
    void requestBlockCard_success() {
        Card card = Card.builder().id(1L).owner(user).status(CardStatus.ACTIVE).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.requestBlockCard(1L, user);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    void requestBlockCard_notOwner() {
        User another = User.builder().id(2L).build();
        Card card = Card.builder().id(1L).owner(another).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(AccessDeniedException.class, () -> cardService.requestBlockCard(1L, user));
    }

    @Test
    void transferBetweenCards_success() {
        Card from = Card.builder().id(1L).owner(user).balance(BigDecimal.valueOf(1000)).build();
        Card to = Card.builder().id(2L).owner(user).balance(BigDecimal.valueOf(500)).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        cardService.transferBetweenCards(1L, 2L, BigDecimal.valueOf(300), user);

        assertEquals(BigDecimal.valueOf(700), from.getBalance());
        assertEquals(BigDecimal.valueOf(800), to.getBalance());
    }

    @Test
    void transferBetweenCards_notEnoughMoney() {
        Card from = Card.builder().id(1L).owner(user).balance(BigDecimal.valueOf(100)).build();
        Card to = Card.builder().id(2L).owner(user).balance(BigDecimal.valueOf(500)).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(CustomException.class, () ->
                cardService.transferBetweenCards(1L, 2L, BigDecimal.valueOf(300), user));
    }

    @Test
    void transferBetweenCards_wrongOwner() {
        User another = User.builder().id(2L).build();
        Card from = Card.builder().id(1L).owner(another).balance(BigDecimal.valueOf(1000)).build();
        Card to = Card.builder().id(2L).owner(another).balance(BigDecimal.valueOf(500)).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(AccessDeniedException.class, () ->
                cardService.transferBetweenCards(1L, 2L, BigDecimal.valueOf(100), user));
    }
}