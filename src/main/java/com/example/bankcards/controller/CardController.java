package com.example.bankcards.controller;
import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    // ================= ADMIN =================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto createCard(@RequestBody CardCreateDto dto) {
        return cardService.createCard(dto);
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public void blockCard(@PathVariable Long id) {
        cardService.blockCard(id);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateCard(@PathVariable Long id) {
        cardService.activateCard(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CardDto> getAllCards(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardService.getAllCards(pageable);
    }

    // ================= USER =================
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CardDto> getUserCards(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        User user = userService.findByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        return cardService.getUserCards(user, pageable);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public void transfer(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestBody TransferDto dto) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.transferBetweenCards(dto.getFromCardId(), dto.getToCardId(), dto.getAmount(), user);
    }

    @PutMapping("/{id}/request-block")
    @PreAuthorize("hasRole('USER')")
    public void requestBlock(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long id) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.requestBlockCard(id, user);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('USER')")
    public BigDecimal getBalance(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id) {
        User user = userService.findByUsername(userDetails.getUsername());
        return cardService.getBalance(id, user);
    }
}