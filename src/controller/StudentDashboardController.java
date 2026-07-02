package controller;

import dao.PaymentDao;
import dao.StudentDao;
import model.Payment;
import model.Student;

import java.util.List;
import java.util.ArrayList;

public class StudentDashboardController {

    private final PaymentDao paymentDao = new PaymentDao();
    private final StudentDao studentDao = new StudentDao();

    /**
     * Returns all payments for a student from the database.
     */
    public List<Payment> getStudentPayments(String studentId) {
        List<Payment> payments = paymentDao.getPaymentsByStudentId(studentId);
        // Return empty list (not null) if no payments found
        return payments != null ? payments : new ArrayList<>();
    }

    /**
     * Checks whether a student has paid for the current semester.
     * Used to determine QR pass access status.
     */
    public boolean hasPaidCurrentSemester(String studentId) {
        return paymentDao.hasPaidCurrentSemester(studentId);
    }

    /**
     * Returns hardcoded announcements for now.
     * TODO: replace with DB-backed announcements table later.
     */
    public List<String> getAnnouncements() {
        List<String> announcements = new ArrayList<>();
        announcements.add("Water shutdown this Saturday | Off from 10am to 2pm for maintenance");
        announcements.add("Hostel Week | Games and food this Friday at 6pm in the common room");
        announcements.add("Exam quiet hours | Extended from 10pm to 8am during exam period");
        announcements.add("New Wi-Fi password | Changed to: Hostel@2024");
        announcements.add("Room inspection | This Thursday between 2pm–5pm");
        return announcements;
    }

    /**
     * Reloads student data from DB by username.
     */
    public Student getStudentDetails(String username) {
        return studentDao.getStudentByUsername(username);
    }
}