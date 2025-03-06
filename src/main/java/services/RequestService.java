package services;

import entities.Driver;
import entities.Request;
import entities.User;
import entities.Location;
import utils.DataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestService implements IService<Request> {
    private final Connection connection;

    public RequestService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Request request) throws SQLException {
        // Check if Client and Locations are not null
        if (request.getClient() == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (request.getDepartureLocation() == null || request.getArrivalLocation() == null) {
            throw new IllegalArgumentException("Departure and Arrival locations cannot be null");
        }

        // SQL query for inserting a new request
        String sql = "INSERT INTO request (id_client, id_departure_location, id_arrival_location, status, request_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, request.getClient().getId()); // Set client ID
            preparedStatement.setInt(2, request.getDepartureLocation().getId()); // Set departure location ID
            preparedStatement.setInt(3, request.getArrivalLocation().getId()); // Set arrival location ID
            preparedStatement.setString(4, request.getStatus().toString()); // Set request status
            preparedStatement.setTimestamp(5, Timestamp.valueOf(request.getRequestDate())); // Set request date
            preparedStatement.executeUpdate(); // Execute the insertion
            System.out.println("✅ Request created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error creating request", e);
        }
        return false;
    }


    @Override
    public void update(Request request) throws SQLException {
        String sql = "UPDATE request SET id_client = ?, id_departure_location = ?, id_arrival_location = ?, status = ?, request_date = ? WHERE id_request = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, request.getClient().getId());
        preparedStatement.setInt(2, request.getDepartureLocation().getId());
        preparedStatement.setInt(3, request.getArrivalLocation().getId());
        preparedStatement.setString(4, request.getStatus().toString());
        preparedStatement.setTimestamp(5, Timestamp.valueOf(request.getRequestDate()));
        preparedStatement.setInt(6, request.getIdRequest());
        preparedStatement.executeUpdate();
        System.out.println("✅ Request updated successfully");
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM request WHERE id_request = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Request deleted successfully");
    }

    @Override
    public List<Request> read() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = """
                    SELECT r.id_request, r.id_client, r.id_departure_location, r.id_arrival_location, r.status, r.request_date,
                           u.name AS client_name,
                           l1.address AS departure_address, l2.address AS arrival_address
                    FROM request r
                    JOIN user u ON r.id_client = u.id_user
                    JOIN location l1 ON r.id_departure_location = l1.id_location
                    JOIN location l2 ON r.id_arrival_location = l2.id_location
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {


            UserService userService = new UserService();
            LocationService locationService = new LocationService();

            while (resultSet.next()) {

                User client = userService.getById(resultSet.getInt("id_client"));
                Location departure = locationService.getById(resultSet.getInt("id_departure_location"));
                Location arrival = locationService.getById(resultSet.getInt("id_arrival_location"));


                Request request = new Request(
                        resultSet.getInt("id_request"),
                        client,
                        departure, arrival,
                        Request.RequestStatus.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("request_date").toLocalDateTime()
                );

                requests.add(request);
            }
        }
        return requests;
    }

    public Request getById(int id) throws SQLException {
        String sql = "SELECT * FROM `request` WHERE id_request = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    UserService userService = new UserService();
                    LocationService locationService = new LocationService();

                    // Récupérer les objets avec leurs services respectifs
                    User client = userService.getById(rs.getInt("id_client"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));


                    Request request = new Request(
                            rs.getInt("id_request"),
                            client,
                            departure, arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    return request;
                }
            }
        }
        return null;
    }

    public List<Request> getByClient(User client) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM request WHERE id_client = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, client.getId());
            try (ResultSet rs = ps.executeQuery()) {

                UserService userService = new UserService();
                LocationService locationService = new LocationService();

                while (rs.next()) {

                    User clientFromDb = userService.getById(rs.getInt("id_client"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));


                    Request request = new Request(
                            rs.getInt("id_request"),
                            clientFromDb,
                            departure, arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    requests.add(request);
                }
            }
        }
        return requests;
    }

    public List<Request> getByStatus(Request.RequestStatus status) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM request WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
            try (ResultSet rs = ps.executeQuery()) {

                UserService userService = new UserService();
                LocationService locationService = new LocationService();

                while (rs.next()) {

                    User client = userService.getById(rs.getInt("id_client"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));


                    Request request = new Request(
                            rs.getInt("id_request"),
                            client,
                            departure, arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    requests.add(request);
                }
            }
        }
        return requests;
    }

    public List<Request> getRequestsByUserId(int userId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM request WHERE id_client = ?";  // Query to get requests for a specific user

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);  // Set the user ID parameter

            try (ResultSet rs = ps.executeQuery()) {

                UserService userService = new UserService();
                LocationService locationService = new LocationService();

                // Iterate over the result set and create Request objects
                while (rs.next()) {
                    User client = userService.getById(rs.getInt("id_client"));  // Get the user for this request
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));  // Get the departure location
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));  // Get the arrival location

                    // Create the Request object from the result set data
                    Request request = new Request(
                            rs.getInt("id_request"),
                            client,
                            departure,
                            arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    requests.add(request);  // Add the request to the list
                }
            }
        }
        return requests;
    }

    public int countRequests() throws SQLException {
        String query = "SELECT COUNT(*) FROM request"; // Replace 'requests' with your actual table name
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt(1); // Get the count from the result set
            }
        }
        return 0; // Return 0 if something goes wrong or no rows are found
    }

    public int countRequestsByStatus(Request.RequestStatus status) throws SQLException {
        String query = "SELECT COUNT(*) FROM requests WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status.name());  // Set the enum as a string
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // Return the count
            }
        }
        return 0;
    }


    public List<Request> searchRequests(Map<String, Object> filters) throws SQLException {
        List<Request> requests = new ArrayList<>();

        // Début de la requête de base
        StringBuilder sql = new StringBuilder("SELECT r.id_request, r.id_client, r.id_departure_location, r.id_arrival_location, r.status, r.request_date, ");
        sql.append("u.name AS client_name, ");
        sql.append("l1.address AS departure_address, l2.address AS arrival_address ");
        sql.append("FROM request r ");
        sql.append("JOIN user u ON r.id_client = u.id_user ");
        sql.append("JOIN location l1 ON r.id_departure_location = l1.id_location ");
        sql.append("JOIN location l2 ON r.id_arrival_location = l2.id_location ");
        sql.append("WHERE 1=1 "); // Condition toujours vraie pour faciliter l'ajout des filtres

        // Liste des paramètres
        List<Object> parameters = new ArrayList<>();

        // Dynamique: ajout des filtres
        if (filters.containsKey("status")) {
            sql.append("AND r.status = ? ");
            parameters.add(filters.get("status").toString()); // Ajout du statut dans les paramètres
        }
        if (filters.containsKey("clientId")) {
            sql.append("AND r.id_client = ? ");
            parameters.add(filters.get("clientId")); // Ajout du client ID dans les paramètres
        }
        if (filters.containsKey("departureLocation")) {
            sql.append("AND r.id_departure_location = ? ");
            parameters.add(filters.get("departureLocation")); // Ajout du départ location dans les paramètres
        }
        if (filters.containsKey("arrivalLocation")) {
            sql.append("AND r.id_arrival_location = ? ");
            parameters.add(filters.get("arrivalLocation")); // Ajout du arrival location dans les paramètres
        }
        // Ajouter d'autres filtres si nécessaire

        // Préparation de la requête
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            // Assignation des paramètres dans la requête préparée
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));  // Paramètre dynamique
            }

            // Exécution de la requête
            try (ResultSet rs = ps.executeQuery()) {
                UserService userService = new UserService();
                LocationService locationService = new LocationService();

                while (rs.next()) {
                    User client = userService.getById(rs.getInt("id_client"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));

                    Request request = new Request(
                            rs.getInt("id_request"),
                            client,
                            departure, arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    requests.add(request);
                }
            }
        }

        return requests;
    }


}






