package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Announcement;
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
        String sql = "INSERT INTO announcement (id_transporter, title, content, date, zone, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //preparedStatement.setInt(1, announcement.getIdTransporter().getId());
        preparedStatement.setInt(1, 1);
        preparedStatement.setString(2, announcement.getTitle());
        preparedStatement.setString(3, announcement.getContent());
        preparedStatement.setObject(4, announcement.getDate());
        preparedStatement.setString(5, announcement.getZone().toString());
        preparedStatement.setInt(6, announcement.getStatus());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Announcement announcement) throws SQLException {
        String sql = "UPDATE announcement SET id_transporter = ?, title = ?, content = ?, date = ?, zone = ?, status = ? WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 1);
        preparedStatement.setString(2, announcement.getTitle());
        preparedStatement.setString(3, announcement.getContent());
        preparedStatement.setObject(4, announcement.getDate());
        preparedStatement.setString(5, announcement.getZone().toString());
        preparedStatement.setInt(6, announcement.getStatus());
        preparedStatement.setInt(7, announcement.getIdAnnouncement());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM announcement WHERE id_announcement = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
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

            // Récupérer le Driver correspondant à id_transporter
//            Driver driver = new Driver();
//            driver.setId(rs.getInt("id_transporter"));

            announcement.setIdTransporter(1);

            announcement.setTitle(rs.getString("title"));
            announcement.setContent(rs.getString("content"));
            announcement.setDate(rs.getObject("date", Timestamp.class).toLocalDateTime());
            announcement.setZone(Announcement.Zone.valueOf(rs.getString("zone")));
            announcement.setStatus(rs.getInt("status"));

            announcements.add(announcement);
        }
        return announcements;
    }


}