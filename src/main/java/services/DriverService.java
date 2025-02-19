package services;

import entities.Driver;
import entities.Location;
import entities.User;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverService implements IService<Driver> {
    private final Connection connection;

    public DriverService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Driver driver) throws SQLException {
        String sqlCheckUser = "SELECT COUNT(*) FROM `user` WHERE `id_user` = ?";
        try (PreparedStatement psCheckUser = connection.prepareStatement(sqlCheckUser)) {
            psCheckUser.setInt(1, driver.getUser().getId());
            try (ResultSet rs = psCheckUser.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    String sqlDriver = "INSERT INTO `driver`(`id_user`, `permit_number`, `role`, `status`) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
                        psDriver.setInt(1, driver.getUser().getId());
                        psDriver.setString(2, driver.getPermitNumber());
                        psDriver.setString(3, driver.getDriverRole().name());
                        psDriver.setInt(4, driver.getDriverStatus());
                        psDriver.executeUpdate();
                        return true;
                    }
                } else {
                    throw new SQLException("User does not exist. Cannot add driver.");
                }
            }
        }
    }

    @Override
    public void update(Driver driver) throws SQLException {
        String sqlDriver = "UPDATE `driver` SET `permit_number`=?, `role`=?, `status`=? WHERE `id_user` = ?";
        try (PreparedStatement psDriver = connection.prepareStatement(sqlDriver)) {
            psDriver.setString(1, driver.getPermitNumber());
            psDriver.setString(2, driver.getDriverRole().name());
            psDriver.setInt(3, driver.getDriverStatus());
            psDriver.setInt(4, driver.getUser().getId());
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
                "LEFT JOIN location l ON u.id_location = l.id_location";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id_user"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone_number"));
                user.setRole(User.Role.valueOf(rs.getString("user_role")));
                user.setGender(User.Gender.valueOf(rs.getString("gender")));
                user.setProfilePicture(rs.getString("profile_picture"));
                user.setVerified(rs.getBoolean("is_verified"));
                user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                user.setStatus(User.Status.valueOf(rs.getString("user_status")));

                Location location = new Location(
                        rs.getInt("id_location"),
                        rs.getString("address"),
                        rs.getBigDecimal("latitude").floatValue(),
                        rs.getBigDecimal("longitude").floatValue()
                );
                user.setLocation(location);

                Driver driver = new Driver();
                driver.setUser(user);
                driver.setIdDriver(rs.getInt("id_driver"));
                driver.setPermitNumber(rs.getString("permit_number"));
                driver.setDriverRole(Driver.DriverRole.valueOf(rs.getString("role")));
                driver.setDriverStatus(rs.getInt("status"));

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
                "LEFT JOIN location l ON u.id_location = l.id_location " +
                "WHERE d.id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id_user"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone_number"));
                    user.setRole(User.Role.valueOf(rs.getString("user_role")));
                    user.setGender(User.Gender.valueOf(rs.getString("gender")));
                    user.setProfilePicture(rs.getString("profile_picture"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setAccountStatus(User.AccountStatus.valueOf(rs.getString("account_status")));
                    user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
                    user.setStatus(User.Status.valueOf(rs.getString("user_status")));

                    Driver driver = new Driver();
                    driver.setUser(user);
                    driver.setIdDriver(rs.getInt("id_driver"));
                    driver.setPermitNumber(rs.getString("permit_number"));
                    driver.setDriverRole(Driver.DriverRole.valueOf(rs.getString("role")));
                    driver.setDriverStatus(rs.getInt("status"));

                    return driver;
                }
            }
        }
        return null;
    }


}

