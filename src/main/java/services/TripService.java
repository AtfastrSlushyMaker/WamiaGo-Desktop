package services;

import entities.Driver;
import entities.Trip;
import entities.Vehicle;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripService implements IService<Trip> {

    private final Connection connection;

    public TripService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Trip trip) throws SQLException {
        String query = "INSERT INTO trip (departure_city, arrival_city, departure_date, available_seats, price_per_passenger, id_driver, id_vehicle) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trip.getDepartureCity());
            stmt.setString(2, trip.getArrivalCity());
            stmt.setDate(3, new java.sql.Date(trip.getDepartureDate().getTime()));
            stmt.setInt(4, trip.getAvailableSeats());
            stmt.setDouble(5, trip.getPricePerPassenger());
            stmt.setInt(6, trip.getDriver().getIdDriver());
            stmt.setInt(7, trip.getVehicle().getIdVehicle());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Trip trip) throws SQLException {
        String query = "UPDATE trip SET departure_city = ?, arrival_city = ?, departure_date = ?, available_seats = ?, price_per_passenger = ?, id_driver = ?, id_vehicle = ? WHERE id_trip = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trip.getDepartureCity());
            stmt.setString(2, trip.getArrivalCity());
            stmt.setDate(3, new java.sql.Date(trip.getDepartureDate().getTime()));
            stmt.setInt(4, trip.getAvailableSeats());
            stmt.setDouble(5, trip.getPricePerPassenger());
            stmt.setInt(6, trip.getDriver().getIdDriver());
            stmt.setInt(7, trip.getVehicle().getIdVehicle());
            stmt.setInt(8, trip.getIdTrip());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM trip WHERE id_trip = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Trip> read() throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM trip";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Trip trip = new Trip();
                trip.setIdTrip(rs.getInt("id_trip"));
                trip.setDepartureCity(rs.getString("departure_city"));
                trip.setArrivalCity(rs.getString("arrival_city"));
                trip.setDepartureDate(rs.getDate("departure_date"));
                trip.setAvailableSeats(rs.getInt("available_seats"));
                trip.setPricePerPassenger(rs.getDouble("price_per_passenger"));

                Driver driver = new Driver();
                driver.setIdDriver(rs.getInt("id_driver"));
                trip.setDriver(driver);

                Vehicle vehicle = new Vehicle();
                vehicle.setIdVehicle(rs.getInt("id_vehicle"));
                trip.setVehicle(vehicle);

                trips.add(trip);
            }
        }
        return trips;
    }
}