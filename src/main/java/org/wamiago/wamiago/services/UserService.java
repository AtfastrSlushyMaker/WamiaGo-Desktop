package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {

    private final Connection connection;

    public UserService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO `user`(`name`, `email`, `password`, `phone_number`, `role`, `id_location`, `gender`, `profile_picture`, `is_verified`, `account_status`, `date_of_birth`, `status`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.setString(7, user.getGender().name());
            ps.setString(8, user.getProfilePicture());
            ps.setBoolean(9, user.isVerified());
            ps.setString(10, user.getAccountStatus().name());
            ps.setDate(11, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(12, user.getStatus().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE `user` SET `name`=?, `email`=?, `password`=?, `phone_number`=?, `role`=?, `id_location`=?, `gender`=?, `profile_picture`=?, `is_verified`=?, `account_status`=?, `date_of_birth`=?, `status`=? WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getLocation().getId());
            ps.setString(7, user.getGender().name());
            ps.setString(8, user.getProfilePicture());
            ps.setBoolean(9, user.isVerified());
            ps.setString(10, user.getAccountStatus().name());
            ps.setDate(11, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(12, user.getStatus().name());
            ps.setInt(13, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `user` WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
        }
    }

    @Override
    public List<User> read() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id_user"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone_number"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setLocation(new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getBigDecimal("latitude").floatValue(),
                        rs.getBigDecimal("longitude").floatValue()
                ));
                user.setGender(User.Gender.valueOf(rs.getString("gender")));
                user.setProfilePicture(rs.getString("profile_picture"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setStatus(User.Status.valueOf(rs.getString("status")));

                users.add(user);
            }
        }
        return users;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM `user` u JOIN `location` l ON u.id_location = l.id_location WHERE u.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhone(rs.getString("phone_number"));
                    user.setRole(User.Role.valueOf(rs.getString("role")));
                    user.setLocation(new Location(
                            rs.getInt("id_location"),
                            rs.getString("address"),
                            rs.getBigDecimal("latitude").floatValue(),
                            rs.getBigDecimal("longitude").floatValue()
                    ));
                    user.setGender(User.Gender.valueOf(rs.getString("gender")));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                    user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                    user.setStatus(User.Status.valueOf(rs.getString("status")));

                    return user;
                }
            }
        }
        return null;
    }
}