package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Announcement;
import org.wamiago.wamiago.entities.Location;
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
        // Vérifier si startLocation existe
        if (reservation.getStartLocation() == null || reservation.getStartLocation().getId() == 0) {
            System.out.println(" Annulé : La location de départ n'est pas valide.");
            return;
        }

        // Vérifier si endLocation existe
        if (reservation.getEndLocation() == null || reservation.getEndLocation().getId() == 0) {
            System.out.println(" Annulé : La location d'arrivée n'est pas valide.");
            return;
        }

        // Vérifier si announcement existe
        if (reservation.getAnnouncement() == null || reservation.getAnnouncement().getIdAnnouncement() == 0) {
            System.out.println(" Annulé : L'annonce n'est pas valide.");
            return;
        }

        // Si toutes les vérifications sont passées, procéder à l'insertion
        String sql = "INSERT INTO reservation (date, status, description, id_start_location, id_end_location, id_announcement) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDate()));
        preparedStatement.setString(2, reservation.getStatus().toString());
        preparedStatement.setString(3, reservation.getDescription());
        preparedStatement.setInt(4, reservation.getStartLocation().getId());
        preparedStatement.setInt(5, reservation.getEndLocation().getId());
        preparedStatement.setInt(6, reservation.getAnnouncement().getIdAnnouncement());

        preparedStatement.executeUpdate();
        System.out.println(" Réservation ajoutée avec succès.");
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET date = ?, status = ?, description = ?, id_start_location = ?, id_end_location = ?, id_announcement = ? WHERE id_reservation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDate()));
        preparedStatement.setString(2, reservation.getStatus().toString());
        preparedStatement.setString(3, reservation.getDescription());
        preparedStatement.setInt(4, reservation.getStartLocation().getId());
        preparedStatement.setInt(5, reservation.getEndLocation().getId());
        preparedStatement.setInt(6, reservation.getAnnouncement().getIdAnnouncement());
        preparedStatement.setInt(7, reservation.getIdReservation());

        preparedStatement.executeUpdate();
        System.out.println(" Réservation mise à jour avec succès.");
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

        LocationService locationService = new LocationService();
        AnnouncementService announcementService = new AnnouncementService();

        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(rs.getInt("id_reservation"));
            reservation.setDate(rs.getTimestamp("date").toLocalDateTime());
            reservation.setStatus(Reservation.Status.valueOf(rs.getString("status").toUpperCase()));
            reservation.setDescription(rs.getString("description"));

            // Récupérer startLocation
            Location startLocation = locationService.getById(rs.getInt("id_start_location"));
            reservation.setStartLocation(startLocation);

            // Récupérer endLocation
            Location endLocation = locationService.getById(rs.getInt("id_end_location"));
            reservation.setEndLocation(endLocation);

            // Récupérer announcement
            Announcement announcement = announcementService.getById(rs.getInt("id_announcement"));
            reservation.setAnnouncement(announcement);

            reservations.add(reservation);
        }
        return reservations;
    }

    public Reservation getById(int id) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id_reservation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(rs.getInt("id_reservation"));
            reservation.setDate(rs.getTimestamp("date").toLocalDateTime());
            reservation.setStatus(Reservation.Status.valueOf(rs.getString("status").toUpperCase()));
            reservation.setDescription(rs.getString("description"));

            // Récupérer startLocation
            LocationService locationService = new LocationService();
            Location startLocation = locationService.getById(rs.getInt("id_start_location"));
            reservation.setStartLocation(startLocation);

            // Récupérer endLocation
            Location endLocation = locationService.getById(rs.getInt("id_end_location"));
            reservation.setEndLocation(endLocation);

            // Récupérer announcement
            AnnouncementService announcementService = new AnnouncementService();
            Announcement announcement = announcementService.getById(rs.getInt("id_announcement"));
            reservation.setAnnouncement(announcement);

            return reservation;
        }
        return null;
    }
}