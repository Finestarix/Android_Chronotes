package edu.bluejack19_2.chronotes.utils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHandler {

    private static final int EPOCH = 1000;
    private static final String DELIMITER = ":";

    public static String generateStrongPasswordHash(String password) {

        char[] passwordChars = password.toCharArray();
        byte[] salt = null;
        byte[] hash = new byte[0];

        try {
            salt = getSalt();

            PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordChars, salt, EPOCH, 64 * 8);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }

        return EPOCH + ":" + toHex(salt) + ":" + toHex(hash);
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) {

        String[] parts = storedPassword.split(DELIMITER);
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        int diff = -1;
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();

            diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++)
                diff |= hash[i] ^ testHash[i];

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }

        return diff == 0;
    }

    private static byte[] getSalt() {
        byte[] salt = new byte[16];
        try {
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bigInteger = new BigInteger(1, array);
        String hex = bigInteger.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        return (paddingLength > 0) ? String.format("%0" + paddingLength + "d", 0) + hex : hex;
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }

}
