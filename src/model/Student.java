// model/Student.java
package model;

import java.time.LocalDate;

public class Student extends Person {
    private String course;
    private String roomId;
    private LocalDate enrollmentDate;
    private String status; // Active, Graduated, Suspended
    
    public Student(String id, String name, int age, String course) {
        super(id, name, age);
        this.course = course;
        this.enrollmentDate = LocalDate.now();
        this.status = "Active";
    }
    
    // Getters and setters
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Student-specific methods
    public boolean hasPaymentDue() {
        // Logic to check payment status
        return false;
    }
    
    public String getQRCode() {
        // Generate or retrieve QR code
        return "QR-" + id;
    }
}