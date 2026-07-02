// model/Admin.java
package model;

import java.time.LocalDateTime;

public class Admin extends Person {
    private String username;
    private String password; // In real app, store hashed password
    private String role; // Admin, Manager, Staff
    private LocalDateTime lastLogin;
    
    public Admin(String id, String name, int age, String username, String password) {
        super(id, name, age);
        this.username = username;
        this.password = password;
        this.role = "Staff";
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    // Admin methods from UML
    public boolean login(String username, String password) {
        // Authentication logic
        if (this.username.equals(username) && this.password.equals(password)) {
            this.lastLogin = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    public void manageStudents() {
        System.out.println("Managing students...");
    }
    
    public void manageRooms() {
        System.out.println("Managing rooms...");
    }
    
    public void viewReports() {
        System.out.println("Viewing reports...");
    }
}