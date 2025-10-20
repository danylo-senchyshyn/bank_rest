package com.example.bankcards.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * The type Encryption util.
 */
@Component
public class EncryptionUtil {

    private static final String ALGO = "AES";

    private static String SECRET;

    @Value("${jwt.secret}")
    private String secretFromProperties;

    @PostConstruct
    private void init() {
        SECRET = secretFromProperties;
    }

    /**
     * Encrypt string.
     *
     * @param data the data
     * @return the string
     */
    public static String encrypt(String data) {
        try {
            SecretKey key = new SecretKeySpec(SECRET.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    /**
     * Decrypt string.
     *
     * @param encryptedData the encrypted data
     * @return the string
     */
    public static String decrypt(String encryptedData) {
        try {
            SecretKey key = new SecretKeySpec(SECRET.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}