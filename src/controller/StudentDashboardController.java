package controller;

import model.Payment;
import model.Student;
import dao.PaymentDao;
import dao.StudentDao;
import java.util.List;
import java.util.ArrayList;

public class StudentDashboardController {
    
    private PaymentDao paymentDao;
    private StudentDao studentDao;
    
    public StudentDashboardController() {
        this.paymentDao = new PaymentDao();
        this.studentDao = new StudentDao();
    }
    
    public List<Payment> getStudentPayments(String studentId) {
        // This would normally come from database
        // For now, return sample data
        List<Payment> payments = new ArrayList<>();
        
        payments.add(new Payment("P001", studentId, 450.00, "2024-01-15", "Paid", "Credit Card"));
        payments.add(new Payment("P002", studentId, 450.00, "2024-02-15", "Paid", "Bank Transfer"));
        payments.add(new Payment("P003", studentId, 450.00, "2024-03-01", "Pending", "Cash"));
        
        return payments;
    }
    
    public List<String> getAnnouncements() {
        List<String> announcements = new ArrayList<>();
        announcements.add("Water shutdown this Saturday | The water supply will be off from 10am to 2pm for maintenance");
        announcements.add("Hostel Week celebrations | Join us for games and food this Friday at 6pm in the common room");
        announcements.add("Exam break quiet hours | Quiet hours extended from 10pm to 8am during exam period");
        announcements.add("New Wi-Fi password | The Wi-Fi password has been changed to: Hostel@2024");
        announcements.add("Room inspection | Room inspections will be conducted this Thursday between 2pm-5pm");
        return announcements;
    }
    
    public Student getStudentDetails(String username) {
        return studentDao.getStudentByUsername(username);
    }
}
