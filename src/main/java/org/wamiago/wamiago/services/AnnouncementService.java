package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Announcement;
import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementService implements IService<Announcement> {
    private final Connection connection;

    public AnnouncementService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Announcement announcement) throws SQLException {
        // Vérifier si le transporteur existe dans la table driver
        String checkDriverQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ?";
        PreparedStatement checkDriverStmt = connection.prepareStatement(checkDriverQuery);
        checkDriverStmt.setInt(1, announcement.getTransporter().getId_driver());
        ResultSet driverResult = checkDriverStmt.executeQuery();

        // Si le transporteur n'existe pas, on ne fait rien
        if (driverResult.next() && driverResult.getInt(1) == 0) {
            System.out.println("Annulé : Le chauffeur avec l'ID " + announcement.getTransporter().getId_driver() + " n'existe pas.");
            return;
        }

        // Si le transporteur existe, procéder à l'insertion de l'annonce
        String sql = "INSERT INTO announcement (id_transporter, title, content, date, zone, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, announcement.getTransporter().getId_driver());
        preparedStatement.setString(2, announcement.getTitle());
        preparedStatement.setString(3, announcement.getContent());
        preparedStatement.setObject(4, announcement.getDate());
        preparedStatement.setString(5, announcement.getZone().toString());
        preparedStatement.setBoolean(6, announcement.getStatus());

        // Exécution de la requête d'insertion
        preparedStatement.executeUpdate();
        System.out.println(" Annonce ajoutée avec succès pour le chauffeur avec l'ID " + announcement.getTransporter().getId_driver());
    }

    @Override
    public void update(Announcement announcement) throws SQLException {
        // Vérifier si le transporteur existe dans la table driver
        String checkDriverQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ?";
        PreparedStatement checkDriverStmt = connection.prepareStatement(checkDriverQuery);
        checkDriverStmt.setInt(1, announcement.getTransporter().getId_driver());
        ResultSet driverResult = checkDriverStmt.executeQuery();

        // Si le transporteur n'existe pas, on ne fait rien
        if (driverResult.next() && driverResult.getInt(1) == 0) {
            System.out.println(" Annulé : Le chauffeur avec l'ID " + announcement.getTransporter().getId_driver() + " n'existe pas.");
            return;
        }

        // Si le transporteur existe, procéder à la mise à jour de l'annonce
        String sql = "UPDATE announcement SET id_transporter = ?, title = ?, content = ?, date = ?, zone = ?, status = ? WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, announcement.getTransporter().getId_driver());
        preparedStatement.setString(2, announcement.getTitle());
        preparedStatement.setString(3, announcement.getContent());
        preparedStatement.setObject(4, announcement.getDate());
        preparedStatement.setString(5, announcement.getZone().toString());
        preparedStatement.setBoolean(6, announcement.getStatus());
        preparedStatement.setInt(7, announcement.getIdAnnouncement());
        preparedStatement.executeUpdate();
        System.out.println("Annonce mise à jour avec succès pour le chauffeur avec l'ID " + announcement.getTransporter().getId_driver());
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM announcement WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println(" Annonce supprimée avec succès.");
    }

    @Override
    public List<Announcement> read() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcement";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Announcement announcement = new Announcement();
            announcement.setIdAnnouncement(rs.getInt("id_announcement"));

            // Récupérer le transporteur correspondant à partir de la base de données
            DriverService driverService = new DriverService();
            Driver transporter = driverService.getById(rs.getInt("id_transporter"));
            announcement.setTransporter(transporter);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getObject("date", Timestamp.class).toLocalDateTime());
            announcement.setZone(Announcement.Zone.valueOf(rs.getString("zone")));
            announcement.setStatus(rs.getBoolean("status"));
            announcements.add(announcement);
        }
        return announcements;
    }

    public Announcement getById(int id) throws SQLException {
        String sql = "SELECT * FROM announcement WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            Announcement announcement = new Announcement();
            announcement.setIdAnnouncement(rs.getInt("id_announcement"));

            // Récupérer le transporteur correspondant à partir de la base de données
            DriverService driverService = new DriverService();
            Driver transporter = driverService.getById(rs.getInt("id_transporter"));
            announcement.setTransporter(transporter);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getObject("date", Timestamp.class).toLocalDateTime());
            announcement.setZone(Announcement.Zone.valueOf(rs.getString("zone")));
            announcement.setStatus(rs.getBoolean("status"));

            return announcement;
        }
        return null;
    }
}