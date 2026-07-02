package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 *
 * BCrypt automatically handles salting — no need to store salt separately.
 * Work factor of 12 is a good balance between security and speed.
 */
public class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    /**
     * Hashes a plain text password using BCrypt.
     * Always call this before storing a password in the DB.
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifies a plain text password against a BCrypt hash.
     * Returns true if they match.
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // Not a valid BCrypt hash — treat as plain text (migration case)
            return plainPassword.equals(hashedPassword);
        }
    }

    /**
     * Checks if a stored password is already a BCrypt hash.
     * BCrypt hashes always start with "$2a$" or "$2b$".
     */
    public static boolean isHashed(String password) {
        return password != null &&
               (password.startsWith("$2a$") || password.startsWith("$2b$"));
    }
}