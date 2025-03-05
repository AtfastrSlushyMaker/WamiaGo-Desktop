package controllers.taxi.adminside.ride;
import entities.Location;
import entities.Request;
import entities.Ride;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.RequestService;
import services.LocationService;
import services.RideService;
import services.UserService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RideController {
    @FXML
    private AnchorPane side_ankerpane;
    @FXML
    private FlowPane rideFlowPane;
    @FXML
    private AnchorPane root;

    @FXML
    private Label totalRideLabel;

    private final RideService rideService = new RideService();


    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/admin_side/ride.css").toExternalForm());
        loadRidesIntoFlowPane();
     //updateTotalRidesCount();


    }
 /*   public void updateTotalRidesCount() {
        try {
            // Assuming totalRequests is fetched from the database or service
            int totalRequests = rideService.countRides();
            // Set the label text and apply the style class
            totalRideLabel.setText("Total Requests: " + totalRequests);
            totalRideLabel.getStyleClass().add("total-ride-label");
        } catch (SQLException e) {
            e.printStackTrace();  // Handle any database errors
            // Optionally, show a user-friendly message or log the error
        }
    }*/
    private void loadRidesIntoFlowPane() {
        try {
            // Fetch all rides from the database (not specific to any user)
            List<Ride> rides = rideService.read();  // Assume this method fetches all rides

            // Clear any existing content from the FlowPane
            rideFlowPane.getChildren().clear();

            // Check if no rides were found
            if (rides == null || rides.isEmpty()) {
                System.out.println("No rides available.");
                return;  // Exit if no rides exist
            }

            // Iterate through the rides and display them
            for (Ride ride : rides) {
                if (ride == null || ride.getRequest() == null) {
                    continue;  // Skip any ride or request that is null
                }
                VBox rideCard = createRideCard(ride);
                rideFlowPane.getChildren().add(rideCard);  // Add the card to the FlowPane
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle any SQL errors
            // Optionally, you can display an error message to the user here
        }
    }

    private VBox createRideCard(Ride ride) {
        VBox rideCard = new VBox(10);
        rideCard.setPadding(new Insets(10));
        rideCard.getStyleClass().add("ride-card");
        rideCard.setAlignment(Pos.CENTER);

        // Create image and text box for ride status
        HBox imageAndTextBox = createImageAndTextBoxForRide(ride);
        rideCard.getChildren().add(imageAndTextBox);

        // Show ride start and end location
        Label locationLabel = new Label("From: " + ride.getRequest().getDepartureLocation().getAddress() +
                " To: " + ride.getRequest().getArrivalLocation().getAddress());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        rideCard.getChildren().add(locationLabel);

        // Show ride duration
        Label durationLabel = new Label("Duration: " + ride.getDuration() + " mins");
        durationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        rideCard.getChildren().add(durationLabel);

        // Show ride date
        Label dateLabel = new Label("Date: " + ride.getRideDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        rideCard.getChildren().add(dateLabel);

        // Show ride status (optional)
        Label statusLabel = new Label("Status: " + ride.getStatus().toString());  // Assuming Status is an enum
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        rideCard.getChildren().add(statusLabel);

        // Create buttons
        Button selectButton = createSelectButtonForRide(ride);
        Button cancelButton = createCancelButton(ride, rideCard);

        // Place buttons in an HBox to align them horizontally
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(selectButton, cancelButton);

        rideCard.getChildren().add(buttonContainer); // Add the HBox to the VBox

        // Add hover effect for the ride card
        rideCard.setOnMouseExited(event -> {
            rideCard.setScaleX(1);
            rideCard.setScaleY(1);
        });
        rideCard.setOnMouseEntered(event -> {
            rideCard.setScaleX(1.05);
            rideCard.setScaleY(1.05);
        });

        return rideCard;
    }
    private HBox createImageAndTextBoxForRide(Ride ride) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setFillHeight(true);

        // Set an appropriate image for the ride, for example, a car or a ride icon
        ImageView rideImage = new ImageView(new Image(getClass().getResource("/images/icons/taxi.png").toExternalForm()));
        rideImage.setFitWidth(50);
        rideImage.setFitHeight(50);
        rideImage.setPreserveRatio(true);

        // Display ride status (e.g., "Completed", "Ongoing")
        Text statusText = new Text("Status: " + ride.getStatus());
        statusText.setWrappingWidth(180);
        statusText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(statusText, Priority.ALWAYS);
        hbox.getChildren().addAll(rideImage, statusText);

        return hbox;
    }
    private void openRideDetails(Ride ride) {
        System.out.println("Opening details for Ride ID: " + ride.getIdRide());

        Stage modalStage = new Stage();
        modalStage.setTitle("Ride Details - " + ride.getIdRide());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 193, 0.027);");

        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal");
        modalLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Ride Details");
        titleLabel.getStyleClass().add("modal-label");

        VBox rideDetailsBox = new VBox(8);
        rideDetailsBox.getStyleClass().add("ride-details-box");

        // Displaying the details from the Ride object
        Label arrivalLabel = new Label("Arrival Location: " + ride.getRequest().getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure Location: " + ride.getRequest().getDepartureLocation().getAddress());
        Label driverLabel = new Label("Driver: " + ride.getDriver().getUser().getName());  // Assuming Driver class has a getName() method
        Label distanceLabel = new Label("Distance: " + ride.getDistance() + " km");
        Label durationLabel = new Label("Duration: " + ride.getDuration() + " min");
        Label priceLabel = new Label("Price: " + ride.getPrice() + " TND");
        Label statusLabel = new Label("Status: " + ride.getStatus());
        Label dateLabel = new Label("Date: " + ride.getRideDate().toString());

        arrivalLabel.getStyleClass().add("modal-detail-label");
        departureLabel.getStyleClass().add("modal-detail-label");
        driverLabel.getStyleClass().add("modal-detail-label");
        distanceLabel.getStyleClass().add("modal-detail-label");
        durationLabel.getStyleClass().add("modal-detail-label");
        priceLabel.getStyleClass().add("modal-detail-label");
        statusLabel.getStyleClass().add("modal-detail-label");
        dateLabel.getStyleClass().add("modal-detail-label");

        rideDetailsBox.getChildren().addAll(arrivalLabel, departureLabel, driverLabel, distanceLabel,
                durationLabel, priceLabel, statusLabel, dateLabel);

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("modal-close-button");
        closeButton.setOnAction(e -> modalStage.close());

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll(titleLabel, rideDetailsBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 350);
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/ride.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private Button createSelectButtonForRide(Ride ride) {
        Button selectButton = new Button("Details");
        selectButton.getStyleClass().add("ride-button");
        selectButton.setOnAction(e -> openRideDetails(ride));  // You will need to implement the openRideDetails method
        return selectButton;
    }
    private Button createCancelButton(Ride ride, VBox rideCard) {
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("ride-button-delete");

        deleteButton.setOnAction(event -> {
            try {
                rideService.delete(ride.getIdRide());  // Assuming rideService handles the delete operation
                rideFlowPane.getChildren().remove(rideCard);  // Assuming rideFlowPane contains the ride cards
                System.out.println("Ride ID " + ride.getIdRide() + " deleted.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to delete ride ID " + ride.getIdRide());
            }
        });

        return deleteButton;
    }




}
