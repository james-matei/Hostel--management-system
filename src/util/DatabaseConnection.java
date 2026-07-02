package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // MySQL driver class for version 8+
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hostel_management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // XAMPP default is empty
    
    static {
        try {
            // Load driver when class is loaded
            Class.forName(JDBC_DRIVER);
            System.out.println("✅ MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found in bib folder!");
            System.out.println("Looking in: C:\\Users\\nyama\\OneDrive\\Desktop\\HostelManagementSystem\\bib");
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    // Test method
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Connected to database successfully!");
            System.out.println("Database: hostel_management");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}