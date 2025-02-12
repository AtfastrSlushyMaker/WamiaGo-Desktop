package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverService {

    private final Connection connection;

    public DriverService() {
        connection = DataBase.getInstance().getConnection();
    }

    public void addDriver(Driver driver) throws SQLException {

        String sqlCheckUser = "SELECT COUNT(*) FROM `user` WHERE `id_user` = ?";
        try (PreparedStatement psCheckUser = connection.prepareStatement(sqlCheckUser)) {
            psCheckUser.setInt(1, driver.getId());
            ResultSet rs = psCheckUser.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {

                String sqlDriver = "INSERT INTO `driver`(`id_user`, `permit_number`, `role`, `status`) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
                    psDriver.setInt(1, driver.getId());
                    psDriver.setString(2, driver.getPermit_number());
                    psDriver.setString(3, driver.getDriverRole().name());
                    psDriver.setInt(4, driver.getStatus());
                    psDriver.executeUpdate();
                }
            } else {
                throw new SQLException("User does not exist. Cannot add driver.");
            }
        }
    }

    public void updateDriver(Driver driver) throws SQLException {

        String sqlUser = "UPDATE `user` SET `name`=?, `email`=?, `password`=?, `phone_number`=?, `role`=?, `id_location`=? WHERE id_user = ?";
        try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
            psUser.setString(1, driver.getName());
            psUser.setString(2, driver.getEmail());
            psUser.setString(3, driver.getPassword());
            psUser.setString(4, driver.getPhone());
            psUser.setString(5, "CLIENT");
            psUser.setInt(6, driver.getLocation().getId());
            psUser.setInt(7, driver.getId());
            psUser.executeUpdate();
        }


        String sqlDriver = "UPDATE `driver` SET `permit_number`=?, `role`=?, `status`=? WHERE id_user = ?";
        try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
            psDriver.setString(1, driver.getPermit_number());
            psDriver.setString(2, driver.getDriverRole().name());
            psDriver.setInt(3, driver.getStatus());
            psDriver.setInt(4, driver.getId());
            psDriver.executeUpdate();
        }
    }

    public void deleteDriver(int id) throws SQLException {
        String sqlDriver = "DELETE FROM `driver` WHERE id_user = ?";
        try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
            psDriver.setInt(1, id);
            psDriver.executeUpdate();
        }

        String sqlUser = "DELETE FROM `user` WHERE id_user = ?";
        try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
            psUser.setInt(1, id);
            psUser.executeUpdate();
        }
    }

    public Driver getDriverById(int id) throws SQLException {
        String sql = "SELECT * FROM `driver` d JOIN `user` u ON d.id_user = u.id_user JOIN `location` l ON u.id_location = l.id_location WHERE d.id_user = ?";
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

                return new Driver(
                        rs.getInt("id_driver"),
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password"),
                        Driver.DriverRole.valueOf(rs.getString("role").toUpperCase()),
                        location,
                        rs.getString("permit_number"),
                        rs.getInt("status")
                );
            }
        }
        return null;
    }
    public List<Driver> getAllDrivers() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM `driver` d JOIN `user` u ON d.id_user = u.id_user JOIN `location` l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude")
                );

                Driver driver = new Driver(
                        rs.getInt("id_driver"),
                        rs.getInt("id_user"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password"),
                        Driver.DriverRole.valueOf(rs.getString("role").toUpperCase()),
                        location,
                        rs.getString("permit_number"),
                        rs.getInt("status")
                );

                drivers.add(driver);
            }
        }
        return drivers;
    }
}
