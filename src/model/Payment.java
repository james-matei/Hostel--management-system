package model;

import java.time.LocalDate;

public class Payment {
    private String    paymentId;
    private String    studentId;
    private double    amount;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private String    status;
    private String    paymentMethod;

    // Constructor with LocalDate
    public Payment(String paymentId, String studentId, double amount,
                   LocalDate paymentDate, String status, String paymentMethod) {
        this.paymentId     = paymentId;
        this.studentId     = studentId;
        this.amount        = amount;
        this.paymentDate   = paymentDate;
        this.status        = status;
        this.paymentMethod = paymentMethod;
    }

    // Constructor with String date (for convenience)
    public Payment(String paymentId, String studentId, double amount,
                   String paymentDate, String status, String paymentMethod) {
        this.paymentId     = paymentId;
        this.studentId     = studentId;
        this.amount        = amount;
        this.paymentDate   = LocalDate.parse(paymentDate);
        this.status        = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String    getPaymentId()    { return paymentId; }
    public String    getStudentId()    { return studentId; }
    public double    getAmount()       { return amount; }
    public LocalDate getPaymentDate()  { return paymentDate; }
    public LocalDate getDueDate()      { return dueDate; }
    public String    getStatus()       { return status; }
    public String    getPaymentMethod(){ return paymentMethod; }

    // Setters
    public void setPaymentId(String paymentId)       { this.paymentId = paymentId; }
    public void setStudentId(String studentId)       { this.studentId = studentId; }
    public void setAmount(double amount)             { this.amount = amount; }
    public void setPaymentDate(LocalDate paymentDate){ this.paymentDate = paymentDate; }
    public void setDueDate(LocalDate dueDate)        { this.dueDate = dueDate; }
    public void setStatus(String status)             { this.status = status; }
    public void setPaymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; }

    @Override
    public String toString() {
        return paymentDate + " - KSh " + amount + " - " + status;
    }
}