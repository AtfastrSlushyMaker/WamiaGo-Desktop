package services;

import entities.Relocation;
import entities.Reservation;
import utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelocationService implements IService<Relocation> {
    private final Connection connection;
    private final ReservationService reservationService;

    public RelocationService() {
        this.connection = DataBase.getInstance().getConnection();
        this.reservationService = new ReservationService();
    }

    @Override
    public void create(Relocation relocation) throws SQLException {
        if (relocation.getReservation() == null || reservationService.getById(relocation.getReservation().getIdReservation()) == null) {
            System.out.println("Annulé : La réservation associée n'existe pas.");
            return;
        }

        String sql = "INSERT INTO relocation (id_reservation, date, status, cost) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, relocation.getReservation().getIdReservation());
            preparedStatement.setObject(2, relocation.getDate());
            preparedStatement.setBoolean(3, relocation.isStatus());
            preparedStatement.setFloat(4, relocation.getCost());

            preparedStatement.executeUpdate();
            System.out.println("Relocation ajoutée avec succès.");
        }
    }

    @Override
    public void update(Relocation relocation) throws SQLException {
//        if (reservationService.getById(relocation.getReservation().getIdReservation()) == null) {
//            System.out.println("Annulé : La réservation associée n'existe pas.");
//            return;
//        }

        String sql = "UPDATE relocation SET id_reservation = ?, date = ?, status = ?, cost = ? WHERE id_relocation = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, relocation.getReservation().getIdReservation());
            preparedStatement.setObject(2, relocation.getDate());
            preparedStatement.setBoolean(3, relocation.isStatus());
            preparedStatement.setFloat(4, relocation.getCost());
            preparedStatement.setInt(5, relocation.getIdRelocation());

            preparedStatement.executeUpdate();
            System.out.println("Relocation mise à jour avec succès.");
        }
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

        ReservationService reservationService = new ReservationService();

        while (rs.next()) {
            Relocation relocation = new Relocation();
            relocation.setIdRelocation(rs.getInt("id_relocation"));

            // Récupérer la réservation correspondante à partir de la base de données
            Reservation reservation = reservationService.getById(rs.getInt("id_reservation"));
            relocation.setReservation(reservation);

            relocation.setDate(rs.getTimestamp("date"));
            relocation.setStatus(rs.getBoolean("status"));
            relocation.setCost(rs.getFloat("cost"));
            relocations.add(relocation);
        }
        return relocations;
    }

    public List<Relocation> findByFilters(Map<String, Object> filters) throws SQLException {
        List<Relocation> relocations = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM relocation WHERE 1=1 ");
        List<Object> parameters = new ArrayList<>();

        // Construire dynamiquement la requête
        for (String key : filters.keySet()) {
            sql.append(" AND ").append(key).append(" = ?");
            parameters.add(filters.get(key));
        }

        PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());

        // Assigner les valeurs aux paramètres
        for (int i = 0; i < parameters.size(); i++) {
            preparedStatement.setObject(i + 1, parameters.get(i));
        }

        ResultSet rs = preparedStatement.executeQuery();
        ReservationService reservationService = new ReservationService();

        while (rs.next()) {
            Relocation relocation = new Relocation();
            relocation.setIdRelocation(rs.getInt("id_relocation"));

            // Récupérer la réservation associée
            Reservation reservation = reservationService.getById(rs.getInt("id_reservation"));
            relocation.setReservation(reservation);

            relocation.setDate(rs.getTimestamp("date"));
            relocation.setStatus(rs.getBoolean("status"));
            relocation.setCost(rs.getFloat("cost"));

            relocations.add(relocation);
        }
        return relocations;
    }
}