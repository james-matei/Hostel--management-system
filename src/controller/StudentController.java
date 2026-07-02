package controller;

import dao.StudentDao;
import model.Student;
import util.DatabaseConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles actions performed by the student on their own account.
 * e.g. changing their password, updating their contact details.
 */
public class StudentController {

    private final StudentDao studentDao = new StudentDao();

    // ── Password Change ───────────────────────────────────────────────────────

    /**
     * Changes a student's password after verifying the current one.
     * Hashes the new password with BCrypt before storing.
     * Returns true on success, false if current password is wrong or DB fails.
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Student student = studentDao.getStudentByUsername(username);

        if (student == null) {
            System.out.println("Student not found: " + username);
            return false;
        }

        if (!PasswordUtil.verify(currentPassword, student.getPassword())) {
            System.out.println("Current password incorrect for: " + username);
            return false;
        }

        String sql = "UPDATE student SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hash(newPassword));
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Contact Details Update ────────────────────────────────────────────────

    /**
     * Allows a student to update their own email and phone number.
     */
    public boolean updateContactDetails(String studentId, String email, String phone) {
        String sql = "UPDATE person SET email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Refresh Student ───────────────────────────────────────────────────────

    /**
     * Reloads student data from DB by username.
     */
    public Student refreshStudent(String username) {
        return studentDao.getStudentByUsername(username);
    }
}