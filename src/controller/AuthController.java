package controller;

import dao.AdminDao;  // Add this import
import dao.StudentDao; // Add this import
import model.Admin;
import model.Student;

public class AuthController {
    
    private AdminDao adminDao;
    private StudentDao studentDao;
    
    // Static fields for current user
    private static Object currentUser;
    
    public AuthController() {
        this.adminDao = new AdminDao();
        this.studentDao = new StudentDao();
    }
    
    public static void setCurrentUser(Object user) {
        currentUser = user;
    }
    
    public static Object getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public static void logout() {
        currentUser = null;
        System.out.println("User logged out");
    }
    
    /**
     * Login method that checks credentials and returns appropriate user
     * @param username The username (for admin) or student ID (for students)
     * @param password The password
     * @return Object - can be Admin or Student, null if login fails
     */
    public Object login(String username, String password) {
        System.out.println("🔐 Attempting login for: " + username);
        
        // First try to login as Admin
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin != null) {
            System.out.println("✅ Admin found: " + admin.getName());
            
            // Check password (in real app, use password hashing)
            if (admin.getPassword().equals(password)) {
                System.out.println("✅ Admin password correct");
                // Update last login
                adminDao.updateLastLogin(admin.getId());
                return admin;
            } else {
                System.out.println("❌ Admin password incorrect");
                return null;
            }
        }
        
        // If not admin, try as Student (using ID as username)
        Student student = studentDao.getStudentByUsername(username);
        if (student != null) {
            System.out.println("✅ Student found: " + student.getName());
            
            // For students, you might have a default or no password
            // This is simplified - in real app, students might have PIN or use QR codes
            if (password.equals("student123") || password.isEmpty()) {
                System.out.println("✅ Student login successful");
                return student;
            } else {
                System.out.println("❌ Student password incorrect");
                return null;
            }
        }
        
        System.out.println("❌ No user found with username: " + username);
        return null;
    }
    
    /**
     * Check if the logged-in user is an Admin
     */
    public boolean isAdmin(Object user) {
        return user instanceof Admin;
    }
    
    /**
     * Check if the logged-in user is a Student
     */
    public boolean isStudent(Object user) {
        return user instanceof Student;
    }
    
    /**
     * Get the role of the logged-in user
     */
    public String getRole(Object user) {
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            return "ADMIN:" + admin.getRole();
        } else if (user instanceof Student) {
            return "STUDENT";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * Get admin-specific details if user is admin
     */
    public Admin getAdmin(Object user) {
        return (user instanceof Admin) ? (Admin) user : null;
    }
    
    /**
     * Get student-specific details if user is student
     */
    public Student getStudent(Object user) {
        return (user instanceof Student) ? (Student) user : null;
    }
    
    /**
     * Simple password change for admin
     */
    public boolean changeAdminPassword(String adminId, String oldPassword, String newPassword) {
        Admin admin = adminDao.getAdminById(adminId);
        if (admin != null && admin.getPassword().equals(oldPassword)) {
            // In real app, hash the password before storing
            return adminDao.updatePassword(adminId, newPassword);
        }
        return false;
    }
}