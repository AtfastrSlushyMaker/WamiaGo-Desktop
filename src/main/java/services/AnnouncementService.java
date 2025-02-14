package services;

import entities.Announcement;
import entities.Driver;
import utils.DataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnouncementService implements IService<Announcement> {
    private final Connection connection;

    public AnnouncementService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Announcement announcement) throws SQLException {
        String checkDriverQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ?";
        try (PreparedStatement checkDriverStmt = connection.prepareStatement(checkDriverQuery)) {
            checkDriverStmt.setInt(1, announcement.getTransporter().getIdDriver());
            ResultSet driverResult = checkDriverStmt.executeQuery();

            if (driverResult.next() && driverResult.getInt(1) == 0) {
                System.out.println("Erreur : Le chauffeur avec l'ID " + announcement.getTransporter().getIdDriver() + " n'existe pas.");
                return;
            }
        }

        // Insertion dans la table announcement
        String sql = "INSERT INTO announcement (id_transporter, title, content, date, zone, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, announcement.getTransporter().getIdDriver());
            preparedStatement.setString(2, announcement.getTitle());
            preparedStatement.setString(3, announcement.getContent());
            preparedStatement.setObject(4, announcement.getDate());
            preparedStatement.setString(5, announcement.getZone().toString());
            preparedStatement.setBoolean(6, announcement.getStatus());
            preparedStatement.executeUpdate();

            System.out.println("Annonce ajoutée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'annonce : " + e.getMessage());
        }
    }

    @Override
    public void update(Announcement announcement) throws SQLException {
        // Mise à jour de l'annonce
        String sql = "UPDATE announcement SET id_transporter = ?, title = ?, content = ?, date = ?, zone = ?, status = ? WHERE id_announcement = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, announcement.getTransporter().getIdDriver());
            preparedStatement.setString(2, announcement.getTitle());
            preparedStatement.setString(3, announcement.getContent());
            preparedStatement.setObject(4, announcement.getDate());
            preparedStatement.setString(5, announcement.getZone().toString());
            preparedStatement.setBoolean(6, announcement.getStatus());
            preparedStatement.setInt(7, announcement.getIdAnnouncement());

            preparedStatement.executeUpdate();
            System.out.println("Annonce mise à jour avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'annonce : " + e.getMessage());
            throw e;
        }
    }


    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM announcement WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Annonce supprimée avec succès.");
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

            DriverService driverService = new DriverService();
            Driver transporter = driverService.getById(rs.getInt("id_transporter"));
            announcement.setTransporter(transporter);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date") : new Timestamp(System.currentTimeMillis()));
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

            DriverService driverService = new DriverService();
            Driver transporter = driverService.getById(rs.getInt("id_transporter"));
            announcement.setTransporter(transporter);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getTimestamp("date"));
            announcement.setZone(Announcement.Zone.valueOf(rs.getString("zone")));
            announcement.setStatus(rs.getBoolean("status"));

            return announcement;
        }
        return null;
    }

    public List<Announcement> findByFilters(Map<String, Object> filters) throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM announcement WHERE 1=1 ");

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

        while (rs.next()) {
            Announcement announcement = new Announcement();
            announcement.setIdAnnouncement(rs.getInt("id_announcement"));

            DriverService driverService = new DriverService();
            Driver transporter = driverService.getById(rs.getInt("id_transporter"));
            announcement.setTransporter(transporter);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getTimestamp("date"));
            announcement.setZone(Announcement.Zone.valueOf(rs.getString("zone")));
            announcement.setStatus(rs.getBoolean("status"));

            announcements.add(announcement);
        }
        return announcements;
    }

}
