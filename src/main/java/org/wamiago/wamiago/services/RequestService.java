package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Request;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestService implements IService<Request> {

    private final Connection connection;

    public RequestService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Request entity) throws SQLException {

        String checkUserQuery = "SELECT COUNT(*) FROM user WHERE id_user = ?";
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setInt(1, entity.getIdClient());
            try (ResultSet userResult = checkUserStmt.executeQuery()) {
                if (userResult.next() && userResult.getInt(1) == 0) {
                    throw new SQLException("Client ID " + entity.getIdClient() + " does not exist.");
                }
            }
        }


        String checkTaxiQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ? AND role = 'TAXI_DRIVER'";
        try (PreparedStatement checkTaxiStmt = connection.prepareStatement(checkTaxiQuery)) {
            checkTaxiStmt.setInt(1, entity.getIdTaxi());
            try (ResultSet taxiResult = checkTaxiStmt.executeQuery()) {
                if (taxiResult.next() && taxiResult.getInt(1) == 0) {
                    throw new SQLException("Taxi ID " + entity.getIdTaxi() + " does not exist or is not a taxi driver.");
                }
            }
        }

       
        String insertQuery = "INSERT INTO request (id_client, id_taxi, id_departure_location, id_arrival_location, status, request_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, entity.getIdClient());
            preparedStatement.setInt(2, entity.getIdTaxi());
            preparedStatement.setInt(3, entity.getIdDepartureLocation());
            preparedStatement.setInt(4, entity.getIdArrivalLocation());
            preparedStatement.setString(5, entity.getStatus().name());
            preparedStatement.setTimestamp(6, entity.getRequestDate());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Request> read() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String getAllQuery = """
            SELECT r.*, u.name AS client_name, l1.address AS departure_address, l2.address AS arrival_address 
            FROM request r
            JOIN user u ON r.id_client = u.id_user
            JOIN location l1 ON r.id_departure_location = l1.id_location
            JOIN location l2 ON r.id_arrival_location = l2.id_location
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(getAllQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Request request = new Request();
                request.setIdRequest(resultSet.getInt("id_request"));
                request.setIdClient(resultSet.getInt("id_client"));
                request.setIdTaxi(resultSet.getInt("id_taxi"));
                request.setIdDepartureLocation(resultSet.getInt("id_departure_location"));
                request.setIdArrivalLocation(resultSet.getInt("id_arrival_location"));
                request.setStatus(Request.RequestStatus.valueOf(resultSet.getString("status")));
                request.setRequestDate(resultSet.getTimestamp("request_date"));

                System.out.println("Request ID: " + request.getIdRequest());
                System.out.println("Client Name: " + resultSet.getString("client_name"));
                System.out.println("Departure Location: " + resultSet.getString("departure_address"));
                System.out.println("Arrival Location: " + resultSet.getString("arrival_address"));
                System.out.println("Status: " + request.getStatus());
                System.out.println("Request Date: " + request.getRequestDate());
                System.out.println("====================================");

                requests.add(request);
            }
        }
        return requests;
    }

    public Request search(int id) throws SQLException {
        String query = "SELECT * FROM request WHERE id_request = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    Request request = new Request();
                    request.setIdRequest(resultSet.getInt("id_request"));
                    request.setIdClient(resultSet.getInt("id_client"));
                    request.setIdTaxi(resultSet.getInt("id_taxi"));
                    request.setIdDepartureLocation(resultSet.getInt("id_departure_location"));
                    request.setIdArrivalLocation(resultSet.getInt("id_arrival_location"));
                    request.setStatus(Request.RequestStatus.valueOf(resultSet.getString("status").toUpperCase()));
                    request.setRequestDate(resultSet.getTimestamp("request_date"));
                    return request;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM request WHERE id_request = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void update(Request request) throws SQLException {
        String sql = """
            UPDATE request 
            SET id_client = ?, id_taxi = ?, id_departure_location = ?, id_arrival_location = ?, status = ?, request_date = ? 
            WHERE id_request = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, request.getIdClient());
            preparedStatement.setInt(2, request.getIdTaxi());
            preparedStatement.setInt(3, request.getIdDepartureLocation());
            preparedStatement.setInt(4, request.getIdArrivalLocation());
            preparedStatement.setString(5, request.getStatus().toString());
            preparedStatement.setTimestamp(6, request.getRequestDate());
            preparedStatement.setInt(7, request.getIdRequest());
            preparedStatement.executeUpdate();
        }
    }
}
