package services;

import entities.Reclamation;
import entities.User;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {
    private Connection connection;

    public ReclamationService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Reclamation reclamation) throws SQLException {
        String sql = "INSERT INTO reclamation (id_user, content, date, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, reclamation.getUser().getId());
            pstmt.setString(2, reclamation.getContent());
            pstmt.setTimestamp(3, new java.sql.Timestamp(reclamation.getDate().getTime()));
            //pstmt.setInt(4, reclamation.getStatus());
            pstmt.setInt(4, 0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reclamation.setIdReclamation(rs.getInt(1));
                    }
                }
            }

            System.out.println("Reclamation created successfully with ID: " + reclamation.getIdReclamation());
        }
    }

    @Override
    public void update(Reclamation reclamation) throws SQLException {
        String sql = "UPDATE reclamation SET content = ?, date = ?, status = ? WHERE id_reclamation = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reclamation.getContent());
            pstmt.setTimestamp(2, new java.sql.Timestamp(reclamation.getDate().getTime()));
            pstmt.setInt(3, reclamation.getStatus());
            pstmt.setInt(4, reclamation.getIdReclamation());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reclamation updated successfully.");
            } else {
                System.out.println("No reclamation found with ID: " + reclamation.getIdReclamation());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reclamation WHERE id_reclamation = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Reclamation deleted successfully.");
            } else {
                System.out.println("No reclamation found with ID: " + id);
            }
        }
    }

    public List<Reclamation> read() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT r.id_reclamation, r.content, r.date, r.status, " +
                "u.id_user, u.name, u.email, u.phone_number, u.role, u.gender, " +
                "u.profile_picture, u.is_verified, u.account_status, u.date_of_birth, u.status as user_status " +
                "FROM reclamation r " +
                "JOIN user u ON r.id_user = u.id_user";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Create a fully populated User object
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        "",
                        User.Role.valueOf(rs.getString("role")),
                        null,
                        User.Gender.valueOf(rs.getString("gender")),
                        rs.getString("profile_picture"),
                        rs.getBoolean("is_verified"),
                        User.AccountStatus.valueOf(rs.getString("account_status")),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null,
                        User.Status.valueOf(rs.getString("user_status"))
                );


                Reclamation reclamation = new Reclamation(
                        rs.getInt("id_reclamation"),
                        user,
                        rs.getString("content"),
                        rs.getTimestamp("date"),
                        rs.getInt("status")
                );

                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamations: " + e.getMessage());
            throw e; // Re-throw the exception after logging
        }

        return reclamations;
    }
}
