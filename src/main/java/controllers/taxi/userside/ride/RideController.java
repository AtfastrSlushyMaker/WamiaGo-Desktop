package controllers.taxi.userside.ride;
import controllers.taxi.chat.ChatController;
import entities.Ride;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import services.RideService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
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
    private FlowPane rideFlowPane;
    @FXML
    private Button back_to_request;
    @FXML
    private Button See_you_Rides_button;

    private final RideService rideService = new RideService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/ride.css").toExternalForm());
        loadRidesIntoFlowPane();
          setupNavigation();
        back_to_request.setOnAction(event -> handleBackToRequest());
        //See_you_Rides_button.setOnAction(event -> loadScene("/taxi-managment/ride.fxml"));
    }
    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        // Other navigation buttons commented out.
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

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRidesIntoFlowPane() {
        try {
            // Get the logged-in user from the session
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();  // Get the logged-in user

            // Fetch rides for the logged-in user (using the corrected method)
            List<Ride> rides = rideService.getByClient(user);  // This now returns List<Ride> instead of List<Request>

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
        Button chatButton = createChatButton(ride);

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
    private Button createChatButton(Ride ride) {
        Button chatButton = new Button("Chat");
        chatButton.getStyleClass().add("ride-button-chat");
        chatButton.setOnAction(event -> openChatWindow(ride));
        return chatButton;
    }
    private void openChatWindow(Ride ride) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/taxi-managment/chat/chat.fxml"));
            Parent chatRoot = loader.load();

            ChatController chatController = loader.getController();
            chatController.initChat(ride, SessionManager.getInstance().getUser()); // Get user from session manager

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat - Ride ID: " + ride.getIdRide());
            chatStage.setScene(new Scene(chatRoot));
            chatStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










}
