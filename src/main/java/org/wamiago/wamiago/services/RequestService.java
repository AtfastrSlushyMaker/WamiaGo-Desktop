package org.wamiago.wamiago.services;

import entities.Request;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.List;

public class RequestService implements IRequest<Request> {

    private final Connection connection;

    public RequestService() {
        this.connection = DataBase.getInstance().getConnection();
    }


    @Override
    public void create(Request entity) throws SQLException {
        String checkUserQuery = "SELECT COUNT(*) FROM user WHERE id_user = ?";
        PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
        checkUserStmt.setInt(1, entity.getIdClient());
        ResultSet userResult = checkUserStmt.executeQuery();
        if (userResult.next() && userResult.getInt(1) == 0) {
            return;
        }

        String checkTaxiQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ? AND role = 'taxi_driver'";
        PreparedStatement checkTaxiStmt = connection.prepareStatement(checkTaxiQuery);
        checkTaxiStmt.setInt(1, entity.getIdTaxi());
        ResultSet taxiResult = checkTaxiStmt.executeQuery();
        if (taxiResult.next() && taxiResult.getInt(1) == 0) {
            return;
        }

        String insertQuery = "INSERT INTO request (id_client, id_taxi, id_departure_location, id_arrival_location, status, request_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        preparedStatement.setInt(1, entity.getIdClient());
        preparedStatement.setInt(2, entity.getIdTaxi());
        preparedStatement.setInt(3, entity.getIdDepartureLocation());
        preparedStatement.setInt(4, entity.getIdArrivalLocation());
        preparedStatement.setString(5, entity.getStatus().name());
        preparedStatement.setTimestamp(6, new Timestamp(entity.getRequestDate().getTime()));

        preparedStatement.executeUpdate();
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
