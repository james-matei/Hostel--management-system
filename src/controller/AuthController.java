package controller;

import dao.AdminDao;
import dao.StudentDao;
import model.Admin;
import model.Student;
import util.DatabaseConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthController {

    private final AdminDao   adminDao   = new AdminDao();
    private final StudentDao studentDao = new StudentDao();

    private static Object currentUser;

    public static void setCurrentUser(Object user) { currentUser = user; }
    public static Object getCurrentUser()          { return currentUser; }
    public static boolean isAuthenticated()        { return currentUser != null; }

    public static void logout() {
        currentUser = null;
        System.out.println("User logged out");
    }

    /**
     * Attempts login for Admin or Student.
     * Auto-migrates plain text passwords to BCrypt on successful login.
     */
    public Object login(String username, String password) {
        System.out.println("Attempting login for: " + username);

        // ── Try Admin ─────────────────────────────────────────────────────────
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin != null) {
            String stored = admin.getPassword();
            boolean match = PasswordUtil.verify(password, stored);

            if (match) {
                // Auto-migrate plain text to BCrypt if needed
                if (!PasswordUtil.isHashed(stored)) {
                    System.out.println("Migrating admin password to BCrypt...");
                    migrateAdminPassword(admin.getId(), password);
                }
                System.out.println("Admin login successful: " + admin.getName());
                adminDao.updateLastLogin(admin.getId());
                return admin;
            } else {
                System.out.println("Admin password incorrect");
                return null;
            }
        }

        // ── Try Student ───────────────────────────────────────────────────────
        Student student = studentDao.getStudentByUsername(username);
        if (student != null) {
            String stored = student.getPassword();
            boolean match = PasswordUtil.verify(password, stored);

            if (match) {
                // Auto-migrate plain text to BCrypt if needed
                if (!PasswordUtil.isHashed(stored)) {
                    System.out.println("Migrating student password to BCrypt...");
                    migrateStudentPassword(username, password);
                }
                System.out.println("Student login successful: " + student.getName());
                return student;
            } else {
                System.out.println("Student password incorrect");
                return null;
            }
        }

        System.out.println("No user found with username: " + username);
        return null;
    }

    // ── Migration helpers ─────────────────────────────────────────────────────

    private void migrateStudentPassword(String username, String plainPassword) {
        String sql = "UPDATE student SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hash(plainPassword));
            stmt.setString(2, username);
            stmt.executeUpdate();
            System.out.println("✅ Student password migrated to BCrypt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void migrateAdminPassword(String adminId, String plainPassword) {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hash(plainPassword));
            stmt.setString(2, adminId);
            stmt.executeUpdate();
            System.out.println("✅ Admin password migrated to BCrypt");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── Role checks ───────────────────────────────────────────────────────────

    public boolean isAdmin(Object user)   { return user instanceof Admin; }
    public boolean isStudent(Object user) { return user instanceof Student; }

    public String getRole(Object user) {
        if (user instanceof Admin a)  return "ADMIN:" + a.getRole();
        if (user instanceof Student)  return "STUDENT";
        return "UNKNOWN";
    }

    public Admin   getAdmin(Object user)   { return (user instanceof Admin a)   ? a : null; }
    public Student getStudent(Object user) { return (user instanceof Student s) ? s : null; }

    public boolean changeAdminPassword(String adminId, String oldPassword, String newPassword) {
        Admin admin = adminDao.getAdminById(adminId);
        if (admin != null && PasswordUtil.verify(oldPassword, admin.getPassword())) {
            return adminDao.updatePassword(adminId, PasswordUtil.hash(newPassword));
        }
        return false;
    }
}