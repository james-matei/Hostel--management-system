package dao;

import model.Payment;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {

    
    public List<Payment> getPaymentsByStudentId(String studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE studentId = ? ORDER BY paymentDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapPayment(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns all payments in the system (for admin view).
     */
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY paymentDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(mapPayment(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Checks whether a student has a PAID payment for the current semester.
     *
     * Semester logic:
     *   Semester 1 = January  – June      (months 1–6)
     *   Semester 2 = July     – December  (months 7–12)
     *
     * A student is considered paid if there is at least one payment with
     * status = 'Paid' whose paymentDate falls within the current semester.
     */
    public boolean hasPaidCurrentSemester(String studentId) {
        LocalDate now   = LocalDate.now();
        int month       = now.getMonthValue();
        int year        = now.getYear();

        LocalDate semStart = (month <= 6)
            ? LocalDate.of(year, 1, 1)
            : LocalDate.of(year, 7, 1);
        LocalDate semEnd = (month <= 6)
            ? LocalDate.of(year, 6, 30)
            : LocalDate.of(year, 12, 31);

        String sql = "SELECT COUNT(*) FROM payment " +
             "WHERE studentId = ? AND status = 'Paid' " +
             "AND amount >= 4000.00 " +
             "AND paymentDate BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            stmt.setDate(2, Date.valueOf(semStart));
            stmt.setDate(3, Date.valueOf(semEnd));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the most recent payment for a student regardless of status.
     */
    public Payment getLatestPayment(String studentId) {
        String sql = "SELECT * FROM payment WHERE studentId = ? ORDER BY paymentDate DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapPayment(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * Records a new payment. Returns true on success.
     */
    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payment (paymentId, studentId, amount, paymentDate, dueDate, status, paymentMethod) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getStudentId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setDate(4,   Date.valueOf(payment.getPaymentDate()));
            stmt.setDate(5,   payment.getDueDate() != null
                              ? Date.valueOf(payment.getDueDate()) : null);
            stmt.setString(6, payment.getStatus());
            stmt.setString(7, payment.getPaymentMethod());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * Updates the status of a payment (e.g. Pending → Paid).
     */
    public boolean updatePaymentStatus(String paymentId, String status) {
        String sql = "UPDATE payment SET status = ? WHERE paymentId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, paymentId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment(
            rs.getString("paymentId"),
            rs.getString("studentId"),
            rs.getDouble("amount"),
            rs.getDate("paymentDate").toLocalDate(), // use LocalDate constructor
            rs.getString("status"),
            rs.getString("paymentMethod")
        );
        Date due = rs.getDate("dueDate");
        if (due != null) p.setDueDate(due.toLocalDate());
        return p;
    }
}