package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Ride;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideService implements IService <Ride> {

    private final Connection connection;

    public RideService() {
        this.connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void create(Ride entity) throws SQLException {
        String sql = "INSERT INTO ride (id_taxi, id_client, id_request, distance, duration, price, status, ride_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, entity.getIdTaxi());
            preparedStatement.setInt(2, entity.getIdClient());
            preparedStatement.setInt(3, entity.getIdRequest());
            preparedStatement.setDouble(4, entity.getDistance());
            preparedStatement.setInt(5, entity.getDuration());
            preparedStatement.setDouble(6, entity.getPrice());
            preparedStatement.setString(7, entity.getStatus().name());
            preparedStatement.setTimestamp(8, entity.getRideDate());
            preparedStatement.executeUpdate();
        }
    }


    @Override
    public void update(Ride entity) throws SQLException {
        String sql = "UPDATE ride SET id_taxi = ?, id_client = ?, id_request = ?, distance = ?, duration = ?, price = ?, status = ?, ride_date = ? WHERE id_ride = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, entity.getIdTaxi());
            preparedStatement.setInt(2, entity.getIdClient());
            preparedStatement.setInt(3, entity.getIdRequest());
            preparedStatement.setDouble(4, entity.getDistance());
            preparedStatement.setInt(5, entity.getDuration());
            preparedStatement.setDouble(6, entity.getPrice());
            preparedStatement.setString(7, entity.getStatus().name());
            preparedStatement.setTimestamp(8, entity.getRideDate());
            preparedStatement.setInt(9, entity.getIdRide());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ride WHERE id_ride = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Ride> read() throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM ride";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Ride ride = new Ride(
                        resultSet.getInt("id_ride"),
                        resultSet.getInt("id_taxi"),
                        resultSet.getInt("id_client"),
                        resultSet.getInt("id_request"),
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




}
