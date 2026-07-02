package dao;

import model.Student;
import util.DatabaseConnection;
import java.sql.*;

public class StudentDao {  // Note: Class name is StudentDao (not StudentDAO)
    
    public Student getStudentByUsername(String username) {
    String sql = "SELECT p.*, s.course, s.roomId, s.enrollment_date, s.status, s.username, s.password " +
                 "FROM person p JOIN student s ON p.id = s.id WHERE s.username = ?";
                 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("course")
                );
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setRoomId(rs.getString("roomId"));
                student.setStatus(rs.getString("status"));
                
                return student;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Add other methods as needed (getAllStudents, addStudent, etc.)
}