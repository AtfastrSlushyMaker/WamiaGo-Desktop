package services;

import entities.Announcement;
import entities.Location;
import entities.Reservation;
import entities.User;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReservationService implements IService<Reservation> {
    private final Connection connection;
    private final LocationService locationService;
    private final AnnouncementService announcementService;
    private final UserService userService;

    public ReservationService() {
        this.connection = DataBase.getInstance().getConnection();
        this.locationService = new LocationService();
        this.announcementService = new AnnouncementService();
        this.userService = new UserService();
    }

    @Override
    public boolean create(Reservation reservation) throws SQLException {
        if (reservation.getStartLocation() == null || locationService.getById(reservation.getStartLocation().getId()) == null) {
            System.out.println("Annulé : La location de départ n'existe pas.");
            return false;
        }
        if (reservation.getEndLocation() == null || locationService.getById(reservation.getEndLocation().getId()) == null) {
            System.out.println("Annulé : La location d'arrivée n'existe pas.");
            return false;
        }
        if (reservation.getAnnouncement() == null || announcementService.getById(reservation.getAnnouncement().getIdAnnouncement()) == null) {
            System.out.println("Annulé : L'annonce n'existe pas.");
            return false;
        }
        if (reservation.getUser() == null || userService.getById(reservation.getUser().getId()) == null) {
            System.out.println("Annulé : L'utilisateur n'existe pas.");
            return false;
        }

        String sql = "INSERT INTO reservation (date, status, description, id_start_location, id_end_location, id_announcement, id_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, reservation.getDate());
            preparedStatement.setString(2, "ON_GOING");
            preparedStatement.setString(3, reservation.getDescription());
            preparedStatement.setInt(4, reservation.getStartLocation().getId());
            preparedStatement.setInt(5, reservation.getEndLocation().getId());
            preparedStatement.setInt(6, reservation.getAnnouncement().getIdAnnouncement());
            preparedStatement.setInt(7, reservation.getUser().getId());

            preparedStatement.executeUpdate();
            System.out.println("Réservation ajoutée avec succès.");
            return true;
        }
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET date = ?, status = ?, description = ?, id_start_location = ?, id_end_location = ?, id_announcement = ?, id_user = ? WHERE id_reservation = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setObject(1, reservation.getDate());
            preparedStatement.setString(2, reservation.getStatus().toString());
            preparedStatement.setString(3, reservation.getDescription());
            preparedStatement.setInt(4, reservation.getStartLocation().getId());
            preparedStatement.setInt(5, reservation.getEndLocation().getId());
            preparedStatement.setInt(6, reservation.getAnnouncement().getIdAnnouncement());
            preparedStatement.setInt(7, reservation.getUser().getId());
            preparedStatement.setInt(8, reservation.getIdReservation());

            preparedStatement.executeUpdate();
            System.out.println("Réservation mise à jour avec succès.");
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id_reservation = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Reservation> read() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        return reservations;
    }
    public List<Reservation> getReservationsByDriverId(int driverId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.* FROM reservation r " +
                "JOIN announcement a ON r.id_announcement = a.id_announcement " +
                "WHERE a.id_transporter = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, driverId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        return reservations;
    }

    public List<Reservation> getReservationsByClientId(int clientId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE id_user = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        return reservations;
    }


    public Reservation getById(int id) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id_reservation = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        }
        return null;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(rs.getInt("id_reservation"));
        reservation.setDate(rs.getTimestamp("date"));
        reservation.setStatus(Reservation.Status.valueOf(rs.getString("status").toUpperCase()));
        reservation.setDescription(rs.getString("description"));

        reservation.setStartLocation(locationService.getById(rs.getInt("id_start_location")));
        reservation.setEndLocation(locationService.getById(rs.getInt("id_end_location")));
        reservation.setAnnouncement(announcementService.getById(rs.getInt("id_announcement")));
        reservation.setUser(userService.getById(rs.getInt("id_user")));

        return reservation;
    }

    public List<Reservation> findByFilters(Map<String, Object> filters) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM reservation WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        // Construction dynamique de la requête
        for (String key : filters.keySet()) {
            sql.append(" AND ").append(key).append(" = ?");
            parameters.add(filters.get(key));
        }

        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());

        // Assignation des valeurs aux paramètres
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }

        ResultSet rs = preparedStatement.executeQuery();
        LocationService locationService = new LocationService();
        AnnouncementService announcementService = new AnnouncementService();

        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(rs.getInt("id_reservation"));
            reservation.setDate(rs.getTimestamp("date"));
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



}