package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Reservation;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private final Connection connection;

    public ReservationService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (date, status, description, id_start_location, id_end_location, id_announcement) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDate()));
        preparedStatement.setString(2, reservation.getStatus().toString());
        preparedStatement.setString(3, reservation.getDescription());
        preparedStatement.setInt(4, reservation.getIdStartLocation());
        preparedStatement.setInt(5, reservation.getIdEndLocation());
        preparedStatement.setInt(6, reservation.getIdAnnouncement());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET date = ?, status = ?, description = ?, id_start_location = ?, id_end_location = ?, id_announcement = ? WHERE id_reservation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDate()));
        preparedStatement.setString(2, reservation.getStatus().toString());
        preparedStatement.setString(3, reservation.getDescription());
        preparedStatement.setInt(4, reservation.getIdStartLocation());
        preparedStatement.setInt(5, reservation.getIdEndLocation());
        preparedStatement.setInt(6, reservation.getIdAnnouncement());
        preparedStatement.setInt(7, reservation.getIdReservation());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id_reservation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Reservation> read() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(rs.getInt("id_reservation"));
            reservation.setDate(rs.getTimestamp("date").toLocalDateTime());
            reservation.setStatus(Reservation.Status.valueOf(rs.getString("status").toUpperCase()));
            reservation.setDescription(rs.getString("description"));
            reservation.setIdStartLocation(rs.getInt("id_start_location"));
            reservation.setIdEndLocation(rs.getInt("id_end_location"));
            reservation.setIdAnnouncement(rs.getInt("id_announcement"));
            reservations.add(reservation);
        }
        return reservations;
    }
}