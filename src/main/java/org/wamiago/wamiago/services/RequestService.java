package org.wamiago.wamiago.services;

import entities.Request;

import org.wamiago.wamiago.utils.DataBase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class RequestService implements IRequest <Request>{

    private final Connection connection;

    public RequestService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Request entity) throws SQLException {
        String sql = "INSERT INTO request (id_client, id_taxi, id_departure_location, id_arrival_location, status, request_date, client_name, driver_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getIdClient());
            preparedStatement.setString(2, entity.getIdTaxi());
            preparedStatement.setString(3, entity.getIdDepartureLocation());
            preparedStatement.setString(4, entity.getIdArrivalLocation());
            preparedStatement.setString(5, entity.getStatus().name());
            preparedStatement.setTimestamp(6, new Timestamp(entity.getRequestDate().getTime())); // Correction ici
            preparedStatement.setString(7, entity.getClientName());
            preparedStatement.setString(8, entity.getDriverName());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void update(Request entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<Request> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Request getById(int id) throws SQLException {
        return null;
    }
}
