package controller;

import dao.AdminDao;
import dao.StudentDao;
import model.Admin;
import model.Student;

public class AuthController {

    private AdminDao   adminDao;
    private StudentDao studentDao;

    private static Object currentUser;

    public AuthController() {
        this.adminDao   = new AdminDao();
        this.studentDao = new StudentDao();
    }

    public static void setCurrentUser(Object user) { currentUser = user; }
    public static Object getCurrentUser()          { return currentUser; }
    public static boolean isAuthenticated()        { return currentUser != null; }

    public static void logout() {
        currentUser = null;
        System.out.println("User logged out");
    }

    /**
     * Attempts login for either Admin or Student.
     * Admin password is checked against the admin table.
     * Student password is checked against the password stored in the student table.
     *
     * @param username the username entered on the login screen
     * @param password the password entered on the login screen
     * @return Admin or Student object on success, null on failure
     */
    public Object login(String username, String password) {
        System.out.println("Attempting login for: " + username);

        // ── Try Admin first ───────────────────────────────────────────────────
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin != null) {
            if (admin.getPassword().equals(password)) {
                System.out.println("Admin login successful: " + admin.getName());
                adminDao.updateLastLogin(admin.getId());
                return admin;
            } else {
                System.out.println("Admin password incorrect");
                return null; // username matched admin but password wrong — don't fall through
            }
        }

        // ── Try Student ───────────────────────────────────────────────────────
        Student student = studentDao.getStudentByUsername(username);
        if (student != null) {
            if (student.getPassword() != null && student.getPassword().equals(password)) {
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

    // ── Role checks ───────────────────────────────────────────────────────────

    public boolean isAdmin(Object user)   { return user instanceof Admin; }
    public boolean isStudent(Object user) { return user instanceof Student; }

    public String getRole(Object user) {
        if (user instanceof Admin a)   return "ADMIN:" + a.getRole();
        if (user instanceof Student)   return "STUDENT";
        return "UNKNOWN";
    }

    public Admin   getAdmin(Object user)   { return (user instanceof Admin a)   ? a : null; }
    public Student getStudent(Object user) { return (user instanceof Student s) ? s : null; }

    // ── Password change (admin only) ──────────────────────────────────────────

    public boolean changeAdminPassword(String adminId, String oldPassword, String newPassword) {
        Admin admin = adminDao.getAdminById(adminId);
        if (admin != null && admin.getPassword().equals(oldPassword)) {
            return adminDao.updatePassword(adminId, newPassword);
        }
        return false;
    }
}