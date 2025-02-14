package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverService implements IService<Driver> {

    private final Connection connection;

    public DriverService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Driver driver) throws SQLException {
        // Check if the user exists before creating a driver
        String sqlCheckUser = "SELECT COUNT(*) FROM `user` WHERE `id_user` = ?";
        try (PreparedStatement psCheckUser = connection.prepareStatement(sqlCheckUser)) {
            psCheckUser.setInt(1, driver.getId());
            try (ResultSet rs = psCheckUser.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Insert the driver into the database
                    String sqlDriver = "INSERT INTO `driver`(`id_user`, `permit_number`, `role`, `status`) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
                        psDriver.setInt(1, driver.getId());
                        psDriver.setString(2, driver.getPermitNumber());
                        psDriver.setString(3, driver.getDriverRole().name());
                        psDriver.setInt(4, driver.getDriverStatus());
                        psDriver.executeUpdate();
                    }
                } else {
                    throw new SQLException("User does not exist. Cannot add driver.");
                }
            }
        }
    }

    @Override
    public void update(Driver driver) throws SQLException {
        // Update the driver's information in the database
        String sqlDriver = "UPDATE `driver` SET `permit_number`=?, `role`=?, `status`=? WHERE `id_user` = ?";
        try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
            psDriver.setString(1, driver.getPermitNumber());
            psDriver.setString(2, driver.getDriverRole().name());
            psDriver.setInt(3, driver.getDriverStatus());
            psDriver.setInt(4, driver.getId());
            int affectedRows = psDriver.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating driver failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sqlDriver = "DELETE FROM `driver` WHERE `id_user` = ?";
        try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
            psDriver.setInt(1, id);
            int affectedRows = psDriver.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting driver failed, no rows affected.");
            }
        }
    }

    @Override
    public List<Driver> read() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT d.id_driver, d.permit_number, d.role, d.status, " +
                "u.id_user, u.name, u.email, u.phone_number, u.role as user_role, " +
                "l.id_location, l.address, l.latitude, l.longitude, " +
                "u.gender, u.profile_picture, u.is_verified, u.account_status, u.date_of_birth, u.status as user_status " +
                "FROM driver d " +
                "JOIN user u ON d.id_user = u.id_user " +
                "JOIN location l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Driver driver = new Driver();
                driver.setIdDriver(rs.getInt("id_driver")); // Ensure this matches the column name in the database
                driver.setPermitNumber(rs.getString("permit_number"));
                driver.setDriverRole(Driver.DriverRole.valueOf(rs.getString("role")));
                driver.setDriverStatus(rs.getInt("status"));
                driver.setId(rs.getInt("id_user"));
                driver.setName(rs.getString("name"));
                driver.setEmail(rs.getString("email"));
                driver.setPhone(rs.getString("phone_number"));
                driver.setRole(User.Role.valueOf(rs.getString("user_role")));
                driver.setLocation(new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getBigDecimal("latitude").floatValue(),
                        rs.getBigDecimal("longitude").floatValue()
                ));
                driver.setGender(User.Gender.valueOf(rs.getString("gender")));
                driver.setProfilePicture(rs.getString("profile_picture"));
                driver.setVerified(rs.getBoolean("is_verified"));
                driver.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                driver.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                driver.setStatus(User.Status.valueOf(rs.getString("user_status")));

                drivers.add(driver);
            }
        }
        return drivers;
    }

    @Override
    public Driver getById(int id) throws SQLException {
        String sql = "SELECT d.id_driver, d.permit_number, d.role, d.status, " +
                "u.id_user, u.name, u.email, u.phone_number, u.role as user_role, " +
                "l.id_location, l.address, l.latitude, l.longitude, " +
                "u.gender, u.profile_picture, u.is_verified, u.account_status, u.date_of_birth, u.status as user_status " +
                "FROM driver d " +
                "JOIN user u ON d.id_user = u.id_user " +
                "JOIN location l ON u.id_location = l.id_location " +
                "WHERE d.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Driver driver = new Driver();
                    driver.setIdDriver(rs.getInt("id_driver")); // Ensure this matches the column name in the database
                    driver.setPermitNumber(rs.getString("permit_number"));
                    driver.setDriverRole(Driver.DriverRole.valueOf(rs.getString("role")));
                    driver.setDriverStatus(rs.getInt("status"));
                    driver.setId(rs.getInt("id_user"));
                    driver.setName(rs.getString("name"));
                    driver.setEmail(rs.getString("email"));
                    driver.setPhone(rs.getString("phone_number"));
                    driver.setRole(User.Role.valueOf(rs.getString("user_role")));
                    driver.setLocation(new Location(
                            rs.getInt("id_location"),
                            rs.getString("address"),
                            rs.getBigDecimal("latitude").floatValue(),
                            rs.getBigDecimal("longitude").floatValue()
                    ));
                    driver.setGender(User.Gender.valueOf(rs.getString("gender")));
                    driver.setProfilePicture(rs.getString("profile_picture"));
                    driver.setVerified(rs.getBoolean("is_verified"));
                    driver.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                    driver.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                    driver.setStatus(User.Status.valueOf(rs.getString("user_status")));

                    return driver;
                }
            }
        }
        return null;
    }
}