package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CardDto createCard(@RequestBody CardCreateDto dto) {
        return cardService.createCard(dto);
    }

    @PutMapping("/block/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> blockCard(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID карты не может быть null");
        }

        cardService.blockCard(id);
        return ResponseEntity.ok("Карта заблокирована");
    }

    @PutMapping("/unblock/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> unblockCard(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID карты не может быть null");
        }

        cardService.unblockCard(id);
        return ResponseEntity.ok("Карта активирована");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<CardDto> getAllCards(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardService.getAllCards(pageable);
    }

    // ================= ROLE_USER =================
    @GetMapping("/get/users")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Page<CardDto> getUserCards(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        User user = userService.findByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        return cardService.getUserCards(user, pageable);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public void transfer(@AuthenticationPrincipal UserDetails userDetails,
                         @RequestBody TransferDto dto) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.transferBetweenCards(dto.getFromCardId(), dto.getToCardId(), dto.getAmount(), user);
    }

    @PutMapping("/requestBlock/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public void requestBlock(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.requestBlockCard(id, user);
    }

    @GetMapping("/balance/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public BigDecimal getBalance(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id) {
        User user = userService.findByUsername(userDetails.getUsername());
        return cardService.getBalance(id, user);
    }
}