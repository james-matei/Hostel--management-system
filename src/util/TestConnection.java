// TestConnection.java
package util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        System.out.println("Looking for MySQL driver in: bib folder");
        
        try {
            // Try to load the driver manually
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver class found!");
            
            // Try to connect
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("✅ Connected to MySQL!");
                System.out.println("Connection info: " + conn.getMetaData().getURL());
                System.out.println("Database version: " + conn.getMetaData().getDatabaseProductVersion());
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Cannot find MySQL driver class!");
            System.out.println("Make sure mysql-connector JAR is in: C:\\Users\\nyama\\OneDrive\\Desktop\\HostelManagementSystem\\bib");
            System.out.println("The JAR file should be named something like: mysql-connector-j-8.0.33.jar");
        } catch (SQLException e) {
            System.out.println("❌ Cannot connect to database!");
            System.out.println("Check if:");
            System.out.println("1. MySQL is running in XAMPP (green indicator)");
            System.out.println("2. Database 'hostel_management' exists");
            System.out.println("3. Username 'root' with no password is correct");
            System.out.println("\nError details: " + e.getMessage());
        }
    }
}