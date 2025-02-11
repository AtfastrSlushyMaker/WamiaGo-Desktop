package org.wamiago.wamiago.services;
import org.wamiago.wamiago.entities.Response;
import org.wamiago.wamiago.utils.DataBase;

import java.util.List;
import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;

public class ResponseService implements IService<Response> {
    private Connection connection;

    public ResponseService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void createReclamation(Response response) throws SQLException {
        String sql = "INSERT INTO response(id_reclamation,content,date) VALUES(?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, response.getId_reclamation());
            pstmt.setString(2, response.getContent());
            pstmt.setTimestamp(3, new java.sql.Timestamp(response.getDate().getTime()));
            pstmt.executeUpdate();
            System.out.println("Response created successfully.");
        }

    }

    @Override
    public void updateReclamation(Response response) throws SQLException {
        String sql = "UPDATE response SET content=?, date=? WHERE id_reclamation=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, response.getContent());
            pstmt.setTimestamp(2, new java.sql.Timestamp(response.getDate().getTime()));
            pstmt.setInt(3, response.getId_reclamation());
            pstmt.executeUpdate();
            System.out.println("Response updated successfully.");
        }

    }

    @Override
    public void deleteReclamation(int id) throws SQLException {
        String sql = "DELETE FROM response WHERE id_reclamation=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Response deleted successfully.");
        }

    }

    @Override
    public List<Response> read() throws SQLException {
        String sql = "SELECT * FROM response";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Response> responses = new ArrayList<>();
                while (rs.next()) {
                    Response response = new Response();
                    response.setId_reclamation(rs.getInt("id_reclamation"));
                    response.setContent(rs.getString("content"));
                    response.setDate(rs.getTimestamp("date"));
                    responses.add(response);

                }
            }
        }
        return List.of();
    }
}
