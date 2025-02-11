package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Reclamation;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {
    private Connection connection;

    public ReclamationService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void createReclamation(Reclamation reclamation) throws SQLException {
        String sql = "INSERT INTO reclamation (id_user, content, date, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reclamation.getUser().getId());
            pstmt.setString(2, reclamation.getContent());
            pstmt.setTimestamp(3, new java.sql.Timestamp(reclamation.getDate().getTime()));
            pstmt.setInt(4, reclamation.getStatus());

            pstmt.executeUpdate();
            System.out.println("Reclamation created successfully.");
        }
    }


    @Override
    public void updateReclamation(Reclamation reclamation) throws SQLException {
        String sql = "UPDATE reclamation SET content = ?, date = ?, status = ? WHERE id_user = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reclamation.getContent());
            pstmt.setTimestamp(2, new java.sql.Timestamp(reclamation.getDate().getTime()));
            pstmt.setInt(3, reclamation.getStatus());
            pstmt.setInt(4, reclamation.getUser().getId());
            pstmt.executeUpdate();
            System.out.println("Reclamation updated successfully.");

        }
    }

    @Override
    public void deleteReclamation(int id) throws SQLException {
        String sql = "DELETE FROM reclamation WHERE id_reclamtion = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Reclamation deleted successfully.");
        }
    }

    @Override
    public List<Reclamation> read() throws SQLException {
        List<Reclamation> reclamations = new ArrayList<>();
        String sql = "SELECT * FROM reclamation";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id_user"),
                            "",
                            "",
                            "",
                            "",
                            User.Role.CLIENT,
                            null
                    );

                    Reclamation reclamation = new Reclamation(
                            rs.getInt("id_reclamtion"),
                            user,
                            rs.getString("content"),
                            rs.getTimestamp("date"),
                            rs.getInt("status")
                    );

                    reclamations.add(reclamation);
                }
            }
        }
        return reclamations;
    }
}

