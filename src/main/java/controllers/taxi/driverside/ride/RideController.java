package controllers.taxi.driverside.ride;
import entities.Driver;
import entities.Request;
import entities.User;
import entities.Ride;  // Ensure your Ride entity is imported
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.DriverService;
import services.RequestService;
import services.UserService;
import services.RideService;  // Import your RideService
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


public class RideController {
    @FXML
    private Button bookings_button;
    @FXML
    private Button history_button;
    @FXML
    private Button home_button;
    @FXML
    private Button logout_button;
    @FXML
    private Pane pane_1121;
    @FXML
    private Button rides_button;
    @FXML
    private HBox root;
    @FXML
    private AnchorPane side_ankerpane;
    @FXML
    private FlowPane  rideFlowPane;
    @FXML
    private Button request_taxi_button;
    @FXML
    private Button back_to_request;

    private final RequestService requestService = new RequestService();
    private final UserService userService = new UserService();
    private final DriverService driverService = new DriverService();
    private final RideService rideService = new RideService();  // Ride service instance

    // Class-level field to hold the current driver.
    private Driver currentDriver;

    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/driver_side/ride.css").toExternalForm());

        try {
            // Retrieve the logged-in user. (For testing, using a hard-coded ID)
            User loggedInUser = userService.getById(2);

            // Assign the driver to the class-level field.
            currentDriver = driverService.getById(loggedInUser.getId());

            if (currentDriver != null) {
                System.out.println("User is also a driver. Initializing driver-specific logic.");
                loadRidesIntoFlowPane();
                setupNavigation();
            } else {
                System.out.println("User is not a driver.");
                // Optionally handle non-driver users.
            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving the driver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
       // See_you_Rides_button.setOnAction(event -> loadScene("/ride.fxml"));
    }
    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent rootScene = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(rootScene));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBackToRequest() {
        try {
            // Load the FXML for the request scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/taxi-managment/user_side/request.fxml"));
            Parent requestRoot = loader.load();

            // Get the current stage (using home_button as an example)
            Stage stage = (Stage) back_to_request.getScene().getWindow();

            // Set the new scene (requests scene)
            Scene scene = new Scene(requestRoot);
            stage.setScene(scene);

            // Optionally, you can also refresh the view if needed
            // If you need to update something after the scene switch, do it here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadRidesIntoFlowPane() {
        try {
            // Get the logged-in driver from the session
            SessionManager sessionManager = SessionManager.getInstance();
            User driver = sessionManager.getUser();  // Get the logged-in driver

            // Fetch rides for the logged-in driver (assuming a method exists in your service for this)
            List<Ride> rides = rideService.g(driver);  // Assuming this method returns List<Ride> for the driver

            // Clear any existing content from the FlowPane
            rideFlowPane.getChildren().clear();

            // Iterate through the rides and display them
            for (Ride ride : rides) {
                // Print ride details for debugging (remove after testing)
                System.out.println("Arrival Location: " + ride.getRequest().getArrivalLocation().getAddress());
                System.out.println("Departure Location: " + ride.getRequest().getDepartureLocation().getAddress());
                System.out.println("Distance: " + ride.getDistance() + " km");
                System.out.println("Duration: " + ride.getDuration() + " min");
                System.out.println("Price: " + ride.getPrice() + " TND");
                System.out.println("Status: " + ride.getStatus());

                // Create a ride card UI component
                VBox rideCard = createRideCard(ride);
                rideFlowPane.getChildren().add(rideCard);  // Add the card to the FlowPane
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle any SQL errors
        }
    }

}
