package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Relocation;
import org.wamiago.wamiago.entities.Reservation;
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
        // Vérifier si la réservation existe
        if (relocation.getReservation() == null || relocation.getReservation().getIdReservation() == 0) {
            System.out.println(" Annulé : La réservation n'est pas valide.");
            return;
        }

        // Si la vérification est passée, procéder à l'insertion
        String sql = "INSERT INTO relocation (id_reservation, date, status, cost) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, relocation.getReservation().getIdReservation());
        preparedStatement.setObject(2, relocation.getDate());
        preparedStatement.setBoolean(3, relocation.isStatus());
        preparedStatement.setFloat(4, relocation.getCost());

        preparedStatement.executeUpdate();
        System.out.println(" Relocation ajoutée avec succès pour la réservation avec l'ID " + relocation.getReservation().getIdReservation());
    }

    @Override
    public void update(Relocation relocation) throws SQLException {
        String sql = "UPDATE relocation SET id_reservation = ?, date = ?, status = ?, cost = ? WHERE id_relocation = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, relocation.getReservation().getIdReservation());
        preparedStatement.setObject(2, relocation.getDate());
        preparedStatement.setBoolean(3, relocation.isStatus());
        preparedStatement.setFloat(4, relocation.getCost());
        preparedStatement.setInt(5, relocation.getIdRelocation());

        preparedStatement.executeUpdate();
        System.out.println(" Relocation mise à jour avec succès.");
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

            relocation.setDate(rs.getObject("date", Timestamp.class).toLocalDateTime());
            relocation.setStatus(rs.getBoolean("status"));
            relocation.setCost(rs.getFloat("cost"));
            relocations.add(relocation);
        }
        return relocations;
    }
}