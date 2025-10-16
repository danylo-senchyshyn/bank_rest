package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CustomException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    // ================= ADMIN =================
    @Transactional
    public CardDto createCard(CardCreateDto dto) {
        User owner = userRepository.findByUsername(dto.getOwnerUsername())
                .orElseThrow(() -> new CustomException("Пользователь не найден"));

        Card card = Card.builder()
                .number(dto.getNumber()) // здесь можно добавить шифрование
                .owner(owner)
                .expiryDate(dto.getExpiryDate())
                .balance(dto.getBalance())
                .status(CardStatus.ACTIVE)
                .build();

        return mapToDto(cardRepository.save(card));
    }

    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Карта не найдена"));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        if(!cardRepository.existsById(cardId))
            throw new CustomException("Карта не найдена");
        cardRepository.deleteById(cardId);
    }

    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::mapToDto);
    }

    // ================= USER =================
    public Page<CardDto> getUserCards(User user, Pageable pageable) {
        return cardRepository.findAllByOwner(user, pageable).map(this::mapToDto);
    }

    @Transactional
    public void requestBlockCard(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Карта не найдена"));

        if(!card.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Можно блокировать только свои карты");

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void transferBetweenCards(Long fromId, Long toId, BigDecimal amount, User user) {
        Card from = cardRepository.findById(fromId)
                .orElseThrow(() -> new CustomException("Карта отправителя не найдена"));
        Card to = cardRepository.findById(toId)
                .orElseThrow(() -> new CustomException("Карта получателя не найдена"));

        if(!from.getOwner().getId().equals(user.getId()) || !to.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Переводы возможны только между своими картами");

        if(from.getBalance().compareTo(amount) < 0)
            throw new CustomException("Недостаточно средств");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);
    }

    public BigDecimal getBalance(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Карта не найдена"));

        if(!card.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Можно смотреть баланс только своих карт");

        return card.getBalance();
    }

    // ================= HELPERS =================
    private CardDto mapToDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .number(maskCardNumber(card.getNumber()))
                .ownerUsername(card.getOwner().getUsername())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus().name())
                .balance(card.getBalance())
                .build();
    }

    private String maskCardNumber(String number) {
        if(number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}