package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * The type User.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Card> cards;
}