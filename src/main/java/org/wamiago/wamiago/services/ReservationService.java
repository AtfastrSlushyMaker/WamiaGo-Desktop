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
        // 1️⃣ Vérifier si id_start_location existe dans la table location
        String checkStartLocationQuery = "SELECT COUNT(*) FROM location WHERE id_location = ?";
        PreparedStatement checkStartLocationStmt = connection.prepareStatement(checkStartLocationQuery);
        checkStartLocationStmt.setInt(1, reservation.getIdStartLocation());
        ResultSet startLocationResult = checkStartLocationStmt.executeQuery();

        // Si id_start_location n'existe pas, on ne fait rien
        if (startLocationResult.next() && startLocationResult.getInt(1) == 0) {
            System.out.println("❌ Annulé : La location de départ avec l'ID " + reservation.getIdStartLocation() + " n'existe pas.");
            return;
        }

        // 2️⃣ Vérifier si id_end_location existe dans la table location
        String checkEndLocationQuery = "SELECT COUNT(*) FROM location WHERE id_location = ?";
        PreparedStatement checkEndLocationStmt = connection.prepareStatement(checkEndLocationQuery);
        checkEndLocationStmt.setInt(1, reservation.getIdEndLocation());
        ResultSet endLocationResult = checkEndLocationStmt.executeQuery();

        // Si id_end_location n'existe pas, on ne fait rien
        if (endLocationResult.next() && endLocationResult.getInt(1) == 0) {
            System.out.println("❌ Annulé : La location d'arrivée avec l'ID " + reservation.getIdEndLocation() + " n'existe pas.");
            return;
        }

        // 3️⃣ Vérifier si id_announcement existe dans la table announcement
        String checkAnnouncementQuery = "SELECT COUNT(*) FROM announcement WHERE id_announcement = ?";
        PreparedStatement checkAnnouncementStmt = connection.prepareStatement(checkAnnouncementQuery);
        checkAnnouncementStmt.setInt(1, reservation.getIdAnnouncement());
        ResultSet announcementResult = checkAnnouncementStmt.executeQuery();

        // Si id_announcement n'existe pas, on ne fait rien
        if (announcementResult.next() && announcementResult.getInt(1) == 0) {
            System.out.println("❌ Annulé : L'annonce avec l'ID " + reservation.getIdAnnouncement() + " n'existe pas.");
            return;
        }

        // 4️⃣ Si toutes les clés étrangères existent, procéder à l'insertion de la réservation
        String sql = "INSERT INTO reservation (date, status, description, id_start_location, id_end_location, id_announcement) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDate()));
        preparedStatement.setString(2, reservation.getStatus().toString());
        preparedStatement.setString(3, reservation.getDescription());
        preparedStatement.setInt(4, reservation.getIdStartLocation());
        preparedStatement.setInt(5, reservation.getIdEndLocation());
        preparedStatement.setInt(6, reservation.getIdAnnouncement());

        // Exécution de la requête d'insertion
        preparedStatement.executeUpdate();
        System.out.println("✅ Réservation ajoutée avec succès.");
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