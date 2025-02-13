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
        PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
        checkUserStmt.setInt(1, entity.getIdClient());
        ResultSet userResult = checkUserStmt.executeQuery();
        if (userResult.next() && userResult.getInt(1) == 0) {
            return;
        }

        String checkTaxiQuery = "SELECT COUNT(*) FROM driver WHERE id_driver = ? AND role = 'TAXI_DRIVER'";
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
    public List<Request> read() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String getAllQuery = "SELECT * FROM request";

        try (PreparedStatement preparedStatement = connection.prepareStatement(getAllQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int idClient = resultSet.getInt("id_client");
                int idTaxi = resultSet.getInt("id_taxi");
                int idDepartureLocation = resultSet.getInt("id_departure_location");
                int idArrivalLocation = resultSet.getInt("id_arrival_location");

                String departureLocationName = getLocationNameById(idDepartureLocation);
                String arrivalLocationName = getLocationNameById(idArrivalLocation);

                Request request = new Request();
                request.setIdRequest(resultSet.getInt("id_request"));
                request.setIdClient(idClient);
                request.setIdTaxi(idTaxi);
                request.setIdDepartureLocation(idDepartureLocation);
                request.setIdArrivalLocation(idArrivalLocation);
                request.setStatus(Request.RequestStatus.valueOf(resultSet.getString("status")));
                request.setRequestDate(resultSet.getTimestamp("request_date"));

                System.out.println("Request ID: " + request.getIdRequest());
                System.out.println("Client ID: " + idClient);
                System.out.println("Taxi ID: " + idTaxi);
                System.out.println("Departure Location: " + departureLocationName);
                System.out.println("Arrival Location: " + arrivalLocationName);
                System.out.println("Status: " + request.getStatus());
                System.out.println("Request Date: " + request.getRequestDate());
                System.out.println("====================================");

                requests.add(request);
            }
        }

        return requests;
    }

    @Override
    public Request search(int id) throws SQLException {
        String query = "SELECT * FROM request WHERE id_request = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        ResultSet resultSet = stmt.executeQuery();

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

        return null;
    }



    public String getClientNameById(int idClient) throws SQLException {
        String query = "SELECT name FROM user WHERE id_user = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idClient);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return "Unknown";
    }


    public String getLocationNameById(int idLocation) throws SQLException {
        String query = "SELECT address FROM location WHERE id_location = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idLocation);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("address");
        }
        return "Unknown";
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM request WHERE id_request = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Request request) throws SQLException {

        String sql = "UPDATE request SET id_client = ?, id_taxi = ?, id_departure_location = ?, id_arrival_location = ?, status = ?, request_date = ? WHERE id_request = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, request.getIdClient());
        preparedStatement.setInt(2, request.getIdTaxi());
        preparedStatement.setInt(3, request.getIdDepartureLocation());
        preparedStatement.setInt(4, request.getIdArrivalLocation());
        preparedStatement.setString(5, request.getStatus().toString());
        preparedStatement.setTimestamp(6, new Timestamp(request.getRequestDate().getTime()));
        preparedStatement.setInt(7, request.getIdRequest());
        preparedStatement.executeUpdate();
    }



}
