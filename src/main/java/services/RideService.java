package services;

import entities.Ride;
import entities.Request;
import entities.Driver;  // Importer la classe Driver
import utils.DataBase;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideService implements IService<Ride> {
    private final Connection connection;

    public RideService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Ride ride) throws SQLException {

        String sql = "INSERT INTO ride (id_request, id_taxi, distance, duration, price, status, ride_date) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, ride.getRequest().getIdRequest());
        preparedStatement.setInt(2, ride.getDriver().getIdDriver());  // Ajouter l'id du driver
        preparedStatement.setBigDecimal(3, new BigDecimal(ride.getDistance()));
        preparedStatement.setInt(4, ride.getDuration());
        preparedStatement.setBigDecimal(5, new BigDecimal(ride.getPrice()));
        preparedStatement.setString(6, ride.getStatus().toString());
        preparedStatement.setTimestamp(7, ride.getRideDate());
        preparedStatement.executeUpdate();
        System.out.println("✅ Ride created successfully");
    }

    @Override
    public void update(Ride ride) throws SQLException {
        String sql = "UPDATE ride SET id_request = ?, id_taxi = ?, distance = ?, duration = ?, price = ?, status = ?, ride_date = ? WHERE id_ride = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, ride.getRequest().getIdRequest());
        preparedStatement.setInt(2, ride.getDriver().getIdDriver());  // Ajouter l'id du driver
        preparedStatement.setBigDecimal(3, new BigDecimal(ride.getDistance()));
        preparedStatement.setInt(4, ride.getDuration());
        preparedStatement.setBigDecimal(5, new BigDecimal(ride.getPrice()));
        preparedStatement.setString(6, ride.getStatus().toString());
        preparedStatement.setTimestamp(7, ride.getRideDate());
        preparedStatement.setInt(8, ride.getIdRide());
        preparedStatement.executeUpdate();
        System.out.println("✅ Ride updated successfully");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ride WHERE id_ride = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Ride deleted successfully");
    }

    @Override
    public List<Ride> read() throws SQLException {
        List<Ride> rides = new ArrayList<>();

        String sql = "SELECT * FROM ride";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Pour récupérer la Request et le Driver associés
            RequestService requestService = new RequestService();
            DriverService driverService = new DriverService();  // Service pour récupérer un driver

            while (resultSet.next()) {
                Request request = requestService.getById(resultSet.getInt("id_request"));
                Driver driver = driverService.getById(resultSet.getInt("id_taxi"));  // Récupérer le driver
                Ride ride = new Ride(
                        resultSet.getInt("id_ride"),
                        request,
                        driver,
                        resultSet.getDouble("distance"),
                        resultSet.getInt("duration"),
                        resultSet.getDouble("price"),
                        Ride.Status.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("ride_date")
                );
                rides.add(ride);
            }
        }
        return rides;
    }

    public Ride getById(int id) throws SQLException {
        String sql = "SELECT * FROM ride WHERE id_ride = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RequestService requestService = new RequestService();
                    DriverService driverService = new DriverService();
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    return new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                }
            }
        }
        return null;
    }

    // Autres méthodes
    public List<Ride> getByClient(entities.User client) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        // On joint la table ride et request pour filtrer par id_client dans request
        String sql = "SELECT r.* FROM ride r JOIN request req ON r.id_request = req.id_request WHERE req.id_client = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, client.getId());
            try (ResultSet rs = ps.executeQuery()) {
                RequestService requestService = new RequestService();
                DriverService driverService = new DriverService();
                while (rs.next()) {
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,  // Ajouter le driver ici
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);
                }
            }
        }
        return rides;
    }

    public List<Ride> getByStatus(Ride.Status status) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM ride WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            try (ResultSet rs = ps.executeQuery()) {
                RequestService requestService = new RequestService();
                DriverService driverService = new DriverService();
                while (rs.next()) {
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,
                            driver,
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);
                }
            }
        }
        return rides;
    }
}
