package services;

import entities.Request;
import entities.User;
import entities.Driver;
import entities.Location;
import utils.DataBase;
import services.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestService implements IService<Request> {
    private final Connection connection;

    public RequestService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Request request) throws SQLException {
        String sql = "INSERT INTO request (id_client, id_taxi, id_departure_location, id_arrival_location, status, request_date) VALUES (?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, request.getClient().getId());
        preparedStatement.setInt(2, request.getDriver().getIdDriver());
        preparedStatement.setInt(3, request.getDepartureLocation().getId());
        preparedStatement.setInt(4, request.getArrivalLocation().getId());
        preparedStatement.setString(5, request.getStatus().toString());
        preparedStatement.setTimestamp(6, Timestamp.valueOf(request.getRequestDate()));
        preparedStatement.executeUpdate();
        System.out.println("✅ Request created successfully");
    }

    @Override
    public void update(Request request) throws SQLException {
        String sql = "UPDATE request SET id_client = ?, id_taxi = ?, id_departure_location = ?, id_arrival_location = ?, status = ?, request_date = ? WHERE id_request = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, request.getClient().getId());
        preparedStatement.setInt(2, request.getDriver().getIdDriver());
        preparedStatement.setInt(3, request.getDepartureLocation().getId());
        preparedStatement.setInt(4, request.getArrivalLocation().getId());
        preparedStatement.setString(5, request.getStatus().toString());
        preparedStatement.setTimestamp(6, Timestamp.valueOf(request.getRequestDate()));
        preparedStatement.setInt(7, request.getIdRequest());
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
        SELECT r.id_request, r.id_client, r.id_taxi, r.id_departure_location, r.id_arrival_location, r.status, r.request_date,
               u.name AS client_name, d.permit_number AS driver_name,  -- Updated driver column
               l1.address AS departure_address, l2.address AS arrival_address
        FROM request r
        JOIN user u ON r.id_client = u.id_user
        JOIN driver d ON r.id_taxi = d.id_driver
        JOIN location l1 ON r.id_departure_location = l1.id_location
        JOIN location l2 ON r.id_arrival_location = l2.id_location
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Instantiate the services necessary to retrieve the entities
            UserService userService = new UserService();
            DriverService driverService = new DriverService();
            LocationService locationService = new LocationService();

            while (resultSet.next()) {
                // Use services to retrieve the entities
                User client = userService.getById(resultSet.getInt("id_client"));
                Driver driver = driverService.getById(resultSet.getInt("id_taxi"));
                Location departure = locationService.getById(resultSet.getInt("id_departure_location"));
                Location arrival = locationService.getById(resultSet.getInt("id_arrival_location"));

                // Create the Request object with the fetched data
                Request request = new Request(
                        resultSet.getInt("id_request"),
                        client, driver,
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
                    // Créer les instances des services pour récupérer les entités
                    UserService userService = new UserService(); // Assurez-vous que c'est bien instancié comme ça
                    DriverService driverService = new DriverService(); // Service pour récupérer un Driver
                    LocationService locationService = new LocationService(); // Service pour récupérer une Location

                    // Récupérer les objets avec leurs services respectifs
                    User client = userService.getById(rs.getInt("id_client"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));

                    // Créer l'objet Request avec les informations récupérées
                    Request request = new Request(
                            rs.getInt("id_request"),
                            client, driver,
                            departure, arrival,
                            Request.RequestStatus.valueOf(rs.getString("status")),
                            rs.getTimestamp("request_date").toLocalDateTime()
                    );
                    return request;
                }
            }
        }
        return null; // Si aucune demande n'a été trouvée
    }


    public List<Request> getByClient(User client) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT * FROM request WHERE id_client = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, client.getId());
            try (ResultSet rs = ps.executeQuery()) {
                // Instancier les services pour récupérer les entités associées
                UserService userService = new UserService();
                DriverService driverService = new DriverService();
                LocationService locationService = new LocationService();

                while (rs.next()) {
                    // Récupérer les entités associées via les services
                    User clientFromDb = userService.getById(rs.getInt("id_client"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));

                    // Créer l'objet Request avec les informations récupérées
                    Request request = new Request(
                            rs.getInt("id_request"),
                            clientFromDb, driver,
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
                // Instancier les services pour récupérer les entités associées
                UserService userService = new UserService();
                DriverService driverService = new DriverService();
                LocationService locationService = new LocationService();

                while (rs.next()) {
                    // Récupérer les entités associées via les services
                    User client = userService.getById(rs.getInt("id_client"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));
                    Location departure = locationService.getById(rs.getInt("id_departure_location"));
                    Location arrival = locationService.getById(rs.getInt("id_arrival_location"));

                    // Créer l'objet Request avec les informations récupérées
                    Request request = new Request(
                            rs.getInt("id_request"),
                            client, driver,
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
