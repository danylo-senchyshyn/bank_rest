package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface Card repository.
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * Find all by owner page.
     *
     * @param owner    the owner
     * @param pageable the pageable
     * @return the page
     */
    Page<Card> findAllByOwner(User owner, Pageable pageable);

    /**
     * Exists by number boolean.
     *
     * @param number the number
     * @return the boolean
     */
    boolean existsByNumber(String number);
    void deleteAll();
}