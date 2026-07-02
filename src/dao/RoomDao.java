package dao;

import model.Room;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDao {

    /**
     * Returns all rooms from the database ordered by roomId.
     */
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT roomId, capacity, occupied, type, price FROM room ORDER BY roomId";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Room(
                    rs.getString("roomId"),
                    rs.getInt("capacity"),
                    rs.getInt("occupied"),
                    rs.getString("type"),
                    rs.getDouble("price")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns IDs of rooms that still have available beds (occupied < capacity).
     * Used to populate the room dropdown in StudentFormDialog.
     */
    public List<String> getAvailableRoomIds() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT roomId FROM room WHERE occupied < capacity ORDER BY roomId";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) list.add(rs.getString("roomId"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns the names of students currently assigned to a given room.
     */
    public List<String> getOccupantNames(String roomId) {
        List<String> names = new ArrayList<>();
        String sql = "SELECT p.name FROM person p " +
                     "JOIN student s ON p.id = s.id " +
                     "WHERE s.roomId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) names.add(rs.getString("name"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Increments occupied count by 1 when a student is assigned to a room.
     */
    public boolean incrementOccupied(String roomId) {
        String sql = "UPDATE room SET occupied = occupied + 1 WHERE roomId = ? AND occupied < capacity";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Decrements occupied count by 1 when a student is removed from a room.
     */
    public boolean decrementOccupied(String roomId) {
        if (roomId == null || roomId.isEmpty()) return false;
        String sql = "UPDATE room SET occupied = occupied - 1 WHERE roomId = ? AND occupied > 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}