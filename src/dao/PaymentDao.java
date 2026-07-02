package dao;

import model.Payment;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {
    
    public List<Payment> getPaymentsByStudent(String studentId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE studentId = ? ORDER BY paymentDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getString("paymentId"),
                    rs.getString("studentId"),
                    rs.getDouble("amount"),
                    rs.getDate("paymentDate").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("paymentMethod")
                );
                payments.add(payment);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
    
    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payment (paymentId, studentId, amount, paymentDate, status, paymentMethod) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getStudentId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setDate(4, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(5, payment.getStatus());
            stmt.setString(6, payment.getPaymentMethod());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}