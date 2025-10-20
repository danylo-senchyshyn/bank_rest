package com.example.bankcards.util;

/**
 * The type Card util.
 */
public class CardUtil {
    /**
     * Mask card number string.
     *
     * @param number the number
     * @return the string
     */
    public static String maskCardNumber(String number) {
        if(number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}