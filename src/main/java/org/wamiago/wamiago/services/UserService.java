package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Connection connection;

    public UserService() {
        connection = DataBase.getInstance().getConnection();
    }


    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO `user`(`name`, `email`, `password`, `phone_number`, `role`, `id_location`) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.executeUpdate();
        }
    }


    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE `user` SET `name`=?, `email`=?, `phone_number`=?, `password`=?, `role`=?, `id_location`=? WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.setInt(7, user.getId());
            ps.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM `user` WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location WHERE u.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Location location = new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude")
                );

                return new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role").toUpperCase()), // Handle ENUM mapping
                        location
                );
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude")
                );

                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password"),
                        User.Role.valueOf(rs.getString("role").toUpperCase()),
                        location
                );

                users.add(user);
            }
        }
        return users;
    }
}
