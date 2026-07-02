package dao;

import model.Admin;
import util.DatabaseConnection;
import java.sql.*;

public class AdminDao {
    
    // Existing method - getAdminByUsername
    public Admin getAdminByUsername(String username) {
        String sql = "SELECT p.*, a.username, a.password, a.role, a.last_login " +
                     "FROM person p JOIN admin a ON p.id = a.id " +
                     "WHERE a.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                admin.setEmail(rs.getString("email"));
                admin.setPhone(rs.getString("phone"));
                admin.setRole(rs.getString("role"));
                
                return admin;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ADD THIS MISSING METHOD - getAdminById
    public Admin getAdminById(String id) {
        String sql = "SELECT p.*, a.username, a.password, a.role, a.last_login " +
                     "FROM person p JOIN admin a ON p.id = a.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                admin.setEmail(rs.getString("email"));
                admin.setPhone(rs.getString("phone"));
                admin.setRole(rs.getString("role"));
                
                return admin;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Existing method - updateLastLogin
    public boolean updateLastLogin(String adminId) {
        String sql = "UPDATE admin SET last_login = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, adminId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ADD THIS MISSING METHOD - updatePassword
    public boolean updatePassword(String adminId, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, adminId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}