package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Relocation;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelocationService implements IService<Relocation> {
    private final Connection connection;

    public RelocationService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Relocation relocation) throws SQLException {
        String sql = "INSERT INTO relocation (id_reservation, date, status, cost) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, relocation.getIdReservation());
        preparedStatement.setObject(2, relocation.getDate());
        preparedStatement.setInt(3, relocation.getStatus());
        preparedStatement.setFloat(4, relocation.getCost());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Relocation relocation) throws SQLException {
        String sql = "UPDATE relocation SET id_reservation = ?, date = ?, status = ?, cost = ? WHERE id_relocation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, relocation.getIdReservation());
        preparedStatement.setObject(2, relocation.getDate());
        preparedStatement.setInt(3, relocation.getStatus());
        preparedStatement.setFloat(4, relocation.getCost());
        preparedStatement.setInt(5, relocation.getIdRelocation());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM relocation WHERE id_relocation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Relocation> read() throws SQLException {
        List<Relocation> relocations = new ArrayList<>();
        String sql = "SELECT * FROM relocation";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Relocation relocation = new Relocation();
            relocation.setIdRelocation(rs.getInt("id_relocation"));
            relocation.setIdReservation(rs.getInt("id_reservation"));
            relocation.setDate(rs.getObject("date", Timestamp.class).toLocalDateTime());
            relocation.setStatus(rs.getInt("status"));
            relocation.setCost(rs.getFloat("cost"));
            relocations.add(relocation);
        }
        return relocations;
    }
}