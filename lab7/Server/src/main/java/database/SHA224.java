package database;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SHA224 {
    private MessageDigest md;
    private String userSalt; // len = 32

    public SHA224(String dbSalt) {
        try {
            md = MessageDigest.getInstance("SHA-224");
            userSalt = generateSalt();
            md.update((dbSalt + userSalt).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ignored) {}
    }

    public SHA224(String dbSalt, String userSalt) {
        try {
            this.userSalt = userSalt;
            md = MessageDigest.getInstance("SHA-224");
            md.update((dbSalt + userSalt).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ignored) {}
    }

    public String getUserSalt() {
        return userSalt;
    }

    public String getHashString(String password) {
        byte[] messageDigest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return byteToString(messageDigest);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return byteToString(salt);
    }

    private String byteToString(byte[] byteArr) {
        BigInteger no = new BigInteger(1, byteArr);

        // Convert byte array into hex value
        StringBuilder hashString = new StringBuilder(no.toString(16));

        // Add preceding 0s to make it 32 bit
        while (hashString.length() < 32) {
            hashString.insert(0, "0");
        }

        // return the HashText
        return hashString.toString();
    }
}
