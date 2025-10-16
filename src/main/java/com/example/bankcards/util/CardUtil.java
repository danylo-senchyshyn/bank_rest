package com.example.bankcards.util;

public class CardUtil {
    public static String maskCardNumber(String number) {
        if(number == null || number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}