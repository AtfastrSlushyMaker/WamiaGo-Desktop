package services;

import entities.*;
import entities.Driver;
import utils.DataBase;


import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//pdf
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

import static entities.Location.calculateDistance;


public class RideService implements IService<Ride> {
    private final Connection connection;

    public RideService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Ride ride) throws SQLException {
        // Step 1: Retrieve departure and arrival location IDs from the request table
        String getLocationQuery = "SELECT id_departure_location, id_arrival_location FROM request WHERE id_request = ?";
        try (PreparedStatement getLocationStmt = connection.prepareStatement(getLocationQuery)) {
            getLocationStmt.setInt(1, ride.getRequest().getIdRequest());
            try (ResultSet locationResult = getLocationStmt.executeQuery()) {
                if (locationResult.next()) {
                    int departureLocationId = locationResult.getInt("id_departure_location");
                    int arrivalLocationId = locationResult.getInt("id_arrival_location");

                    // Step 2: Calculate distance using LocationService
                    double distance = new Location().calculateDistance(new LocationService().getById(departureLocationId),new LocationService().getById( arrivalLocationId));

                    // Step 3: Insert the ride into the database
                    String sql = "INSERT INTO ride (id_request, id_taxi, distance, duration, price, status, ride_date) VALUES (?,?,?,?,?,?,?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setInt(1, ride.getRequest().getIdRequest());
                        preparedStatement.setInt(2, ride.getDriver().getIdDriver()); // Add driver ID
                        preparedStatement.setBigDecimal(3, new BigDecimal(distance)); // Use calculated distance
                        preparedStatement.setInt(4, ride.getDuration());
                        preparedStatement.setBigDecimal(5, new BigDecimal(ride.getPrice()));
                        preparedStatement.setString(6, ride.getStatus().toString());
                        preparedStatement.setTimestamp(7, ride.getRideDate());
                        preparedStatement.executeUpdate();
                    }
                    System.out.println("✅ Ride created successfully");
                    return true;

                } else {
                    System.out.println("❌ Request not found");
                    return false;
                }
            }
        }
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
    public List<Ride> getByClient(User client) throws SQLException {
        List<Ride> rides = new ArrayList<>();

        // SQL query to fetch rides based on the user's request (client's ID)
        String sql = "SELECT r.* FROM ride r " +
                "JOIN request req ON r.id_request = req.id_request " +
                "WHERE req.id_client = ?";  // Ensure this selects rides based on the user's ID

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, client.getId());  // Set the client's user ID

            try (ResultSet rs = ps.executeQuery()) {
                // Services to fetch associated Request and Driver
                RequestService requestService = new RequestService();
                DriverService driverService = new DriverService();

                while (rs.next()) {
                    // Debugging log to ensure the correct data is fetched
                    System.out.println("Fetching Ride with ID: " + rs.getInt("id_ride"));
                    System.out.println("Request ID: " + rs.getInt("id_request"));
                    System.out.println("Driver ID: " + rs.getInt("id_taxi"));
                    System.out.println("Ride Date: " + rs.getTimestamp("ride_date"));

                    // Fetch the associated Request and Driver
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver driver = driverService.getById(rs.getInt("id_taxi"));

                    // Create a Ride object and add it to the list
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,  // The ride is linked to a request
                            driver,   // The ride is linked to a driver
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            Ride.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);  // Add the ride to the list
                }
            }
        } catch (SQLException e) {
            // Print error details and rethrow exception
            System.err.println("SQL Exception occurred while fetching rides for client: " + client.getId());
            e.printStackTrace();
            throw e;
        }

        // Check if rides were found and print out the total count
        if (rides.isEmpty()) {
            System.out.println("No rides found for client with ID: " + client.getId());
        } else {
            System.out.println("Total rides found: " + rides.size());
        }

        return rides;  // Return the list of rides
    }

    public List<Ride> getRidesByDriver(Driver driver) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = "SELECT * FROM ride WHERE id_taxi = ?";  // Assuming the 'ride' table has an 'id_taxi' column

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, driver.getIdDriver());  // Set the driver's ID in the query
            try (ResultSet rs = ps.executeQuery()) {
                RequestService requestService = new RequestService();  // To fetch associated requests
                DriverService driverService = new DriverService();  // To fetch the driver associated with the ride

                while (rs.next()) {
                    Request request = requestService.getById(rs.getInt("id_request"));
                    Driver rideDriver = driverService.getById(rs.getInt("id_taxi"));  // Fetch the driver for each ride

                    // Safely handling the status conversion from String to Enum
                    Ride.Status status;
                    try {
                        status = Ride.Status.valueOf(rs.getString("status"));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid status value: " + rs.getString("status"));
                        status = Ride.Status.CANCELED;  // Use a default status
                    }

                    // Create a Ride object and add it to the list
                    Ride ride = new Ride(
                            rs.getInt("id_ride"),
                            request,  // The ride is linked to a request
                            rideDriver,  // The ride is linked to a driver
                            rs.getDouble("distance"),
                            rs.getInt("duration"),
                            rs.getDouble("price"),
                            status,  // The ride status
                            rs.getTimestamp("ride_date")
                    );
                    rides.add(ride);  // Add the ride to the list
                }
            }
        }
        return rides;  // Return the list of rides
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


   public static double calculatePrice(Request request) {
        // Exemple de calcul simple basé sur la distance
        // Supposez que vous avez accès aux coordonnées de départ et d'arrivée
        double distance = calculateDistance(request.getDepartureLocation(), request.getArrivalLocation());

        // Tarif de base : 0.900 DT, plus 1 DT par km
        double basePrice = 0.900;
        double pricePerKm = 1.0;
        double price = basePrice + (distance * pricePerKm);
        return price;
    }

    public void updateRideStatus(int rideId, Ride.Status newStatus) throws SQLException {
        String query = "UPDATE ride SET status = ? WHERE id_ride = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newStatus.toString());  // Assuming Status is stored as a String
            preparedStatement.setInt(2, rideId);
            preparedStatement.executeUpdate();
        }
    }








}
