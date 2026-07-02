package model;

import java.time.LocalDate;

public class Student extends Person {

    private String    course;
    private String    roomId;
    private LocalDate enrollmentDate;
    private String    status;
    private String    username;
    private String    password;

    public Student(String id, String name, int age, String course) {
        super(id, name, age);
        this.course         = course;
        this.enrollmentDate = LocalDate.now();
        this.status         = "Active";
    }

    // Course
    public String getCourse()              { return course; }
    public void   setCourse(String course) { this.course = course; }

    // Room
    public String getRoomId()              { return roomId; }
    public void   setRoomId(String roomId) { this.roomId = roomId; }

    // Enrollment date
    public LocalDate getEnrollmentDate()                    { return enrollmentDate; }
    public void      setEnrollmentDate(LocalDate date)      { this.enrollmentDate = date; }

    // Status
    public String getStatus()              { return status; }
    public void   setStatus(String status) { this.status = status; }

    // Username
    public String getUsername()                { return username; }
    public void   setUsername(String username) { this.username = username; }

    // Password
    public String getPassword()                { return password; }
    public void   setPassword(String password) { this.password = password; }

    // Helpers
    public boolean hasPaymentDue() { return false; }
    public String  getQRCode()     { return "QR-" + getId(); }

    @Override
    public String toString() { return getName() + " (ID: " + getId() + ")"; }
}