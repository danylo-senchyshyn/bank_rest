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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * The type Card service.
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    /**
     * Delete all card.
     */
    public void deleteAllCard() {
        cardRepository.deleteAll();
    }

    /**
     * Create card card dto.
     *
     * @param dto the dto
     * @return the card dto
     */
// ================= ADMIN =================
    @Transactional
    public CardDto createCard(CardCreateDto dto) {
        User owner = userRepository.findByUsername(dto.getOwnerUsername())
                .orElseThrow(() -> new CustomException("User not found"));

        if (cardRepository.existsByNumber(dto.getNumber())) {
            throw new CustomException("A card with this number already exists");
        }

        YearMonth ym = YearMonth.parse(dto.getValidTill(), DateTimeFormatter.ofPattern("MM/yy"));
        LocalDate expiryDate = ym.atEndOfMonth();

        Card card = Card.builder()
                .number(dto.getNumber())
                .owner(owner)
                .expiryDate(expiryDate)
                .balance(dto.getBalance())
                .status(CardStatus.ACTIVE)
                .build();

        System.err.println("Creating card for user: " + owner.getUsername() + " with number: " + card.getNumber());

        return mapToDto(cardRepository.save(card));
    }

    /**
     * Block card.
     *
     * @param cardId the card id
     */
    @Transactional
    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Map not found"));
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    /**
     * Unblock card.
     *
     * @param cardId the card id
     */
    @Transactional
    public void unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Map not found"));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    /**
     * Delete card.
     *
     * @param cardId the card id
     */
    @Transactional
    public void deleteCard(Long cardId) {
        if(!cardRepository.existsById(cardId))
            throw new CustomException("Map not found");
        cardRepository.deleteById(cardId);
    }

    /**
     * Gets all cards.
     *
     * @param pageable the pageable
     * @return the all cards
     */
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::mapToDto);
    }

    /**
     * ================= USER ================= @param user the user
     *
     * @param pageable the pageable
     * @return the user cards
     */
    public Page<CardDto> getUserCards(User user, Pageable pageable) {
        return cardRepository.findAllByOwner(user, pageable).map(this::mapToDto);
    }

    /**
     * Request block card.
     *
     * @param cardId the card id
     * @param user   the user
     */
    @Transactional
    public void requestBlockCard(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Map not found"));

        if(!card.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("You can only block your cards");

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    /**
     * Transfer between cards.
     *
     * @param fromId the from id
     * @param toId   the to id
     * @param amount the amount
     * @param user   the user
     */
    @Transactional
    public void transferBetweenCards(Long fromId, Long toId, BigDecimal amount, User user) {
        Card from = cardRepository.findById(fromId)
                .orElseThrow(() -> new CustomException("Sender's card not found"));
        Card to = cardRepository.findById(toId)
                .orElseThrow(() -> new CustomException("Beneficiary's card not found"));

        if(!from.getOwner().getId().equals(user.getId()) || !to.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Transfers are possible only between your cards");

        if(from.getBalance().compareTo(amount) < 0)
            throw new CustomException("Insufficient funds");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        cardRepository.save(from);
        cardRepository.save(to);
    }

    /**
     * Gets balance.
     *
     * @param cardId the card id
     * @param user   the user
     * @return the balance
     */
    public BigDecimal getBalance(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException("Map not found"));

        if(!card.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("You can only view the balance of your cards");

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