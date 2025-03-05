package controllers.taxi.driverside.ride;

import entities.Driver;
import entities.Request;
import entities.User;
import entities.Ride;  // Ensure your Ride entity is imported
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
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
    private FlowPane rideFlowPane;
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

            User loggedInUser = SessionManager.getInstance().getUser();


            currentDriver = driverService.getById(loggedInUser.getId());
            if (currentDriver != null) {
                System.out.println("User is also a driver. Initializing driver-specific logic.");
                loadRidesIntoFlowPane();
                setupNavigation();
            } else {
                System.out.println("User is not a driver.");

            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving the driver: " + e.getMessage());
            e.printStackTrace();
        }
        back_to_request.setOnAction(event -> handleBackToRequest());
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/taxi-managment/driver_side/request.fxml"));
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
            // Get the logged-in user from the session
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();  // Get the logged-in user

            if (user != null) {
                // Fetch the Driver by User ID (assuming you have a method in DriverService)
                Driver driver = driverService.getById(user.getId());

                if (driver != null) {
                    User loggedInUser = SessionManager.getInstance().getUser();


                    currentDriver = driverService.getById(loggedInUser.getId());
                    List<Ride> rides = rideService.getRidesByDriver(driver);  // Passing Driver
                    System.out.println("Nombre de rides récupérés pour le driver " + driver.getIdDriver() + " : " + rides.size());

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
                } else {
                    System.out.println("Driver not found for the logged-in user.");
                }
            } else {
                System.out.println("No logged-in user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle any SQL errors
        }
    }
    private VBox createRideCard(Ride ride) {
        VBox rideCard = new VBox(5); // Réduction de l'espacement
        rideCard.setPadding(new Insets(5)); // Réduction du padding
        rideCard.getStyleClass().add("ride-card");
        rideCard.setAlignment(Pos.CENTER);
        rideCard.setPrefWidth(250); // Définition d'une largeur fixe

        // Create image and text box for ride status
        HBox imageAndTextBox = createImageAndTextBoxForRide(ride);
        rideCard.getChildren().add(imageAndTextBox);

        // Vérification pour éviter les NullPointerException
        if (ride.getRequest() != null && ride.getRequest().getDepartureLocation() != null
                && ride.getRequest().getArrivalLocation() != null) {

            Label locationLabel = new Label("From: " + ride.getRequest().getDepartureLocation().getAddress());
            locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
            locationLabel.setWrapText(true);

            Label destinationLabel = new Label("To: " + ride.getRequest().getArrivalLocation().getAddress());
            destinationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
            destinationLabel.setWrapText(true);

            rideCard.getChildren().addAll(locationLabel, destinationLabel);
        }

        // Create buttons
        Button selectButton = createSelectButtonForRide(ride);
        Button cancelButton = createCancelButton(ride, rideCard);
        Button updateButton = createUpdateButton(ride);

        // Réduire la taille des boutons
        selectButton.setPrefSize(80, 30);
        cancelButton.setPrefSize(80, 30);
        updateButton.setPrefSize(80, 30);

        // Place buttons in an HBox to align them horizontally
        HBox buttonContainer = new HBox(5, selectButton, cancelButton, updateButton);
        buttonContainer.setAlignment(Pos.CENTER);

        rideCard.getChildren().add(buttonContainer);

        // Add hover effect for the ride card
        rideCard.setOnMouseEntered(event -> {
            rideCard.setScaleX(1.03);
            rideCard.setScaleY(1.03);
        });
        rideCard.setOnMouseExited(event -> {
            rideCard.setScaleX(1);
            rideCard.setScaleY(1);
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

        // Null checks for ride attributes
        if (ride == null || ride.getRequest() == null) {
            System.out.println("Invalid ride data.");
            return;  // Handle the case where ride data is not valid
        }

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

        // Safely display ride details
        Label arrivalLabel = new Label("Arrival Location: " + (ride.getRequest().getArrivalLocation() != null ? ride.getRequest().getArrivalLocation().getAddress() : "Unknown"));
        Label departureLabel = new Label("Departure Location: " + (ride.getRequest().getDepartureLocation() != null ? ride.getRequest().getDepartureLocation().getAddress() : "Unknown"));

        Label distanceLabel = new Label("Distance: " + ride.getDistance() + " km");
        Label durationLabel = new Label("Duration: " + ride.getDuration() + " min");
        Label priceLabel = new Label("Price: " + ride.getPrice() + " TND");
        Label statusLabel = new Label("Status: " + ride.getStatus());
        Label dateLabel = new Label("Date: " + ride.getRideDate().toString());
        Label clientLabel = new Label("Client Name: " + ride.getRequest().getClient().getName());


        // Style the labels
        arrivalLabel.getStyleClass().add("modal-detail-label");
        departureLabel.getStyleClass().add("modal-detail-label");

        distanceLabel.getStyleClass().add("modal-detail-label");
        durationLabel.getStyleClass().add("modal-detail-label");
        priceLabel.getStyleClass().add("modal-detail-label");
        statusLabel.getStyleClass().add("modal-detail-label");
        dateLabel.getStyleClass().add("modal-detail-label");

        rideDetailsBox.getChildren().addAll(clientLabel,arrivalLabel, departureLabel,distanceLabel,
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
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/driver_side/ride.css").toExternalForm());
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

    private Button createUpdateButton(Ride ride) {
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("ride-button-update");

        updateButton.setOnAction(event -> openUpdateStatusDialog(ride));

        return updateButton;
    }

    private void openUpdateStatusDialog(Ride ride) {
        RideService rideService1 = new RideService();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Update Ride Status");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        // Status dropdown
        ComboBox<Ride.Status> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Ride.Status.values());
        statusComboBox.setValue(ride.getStatus()); // Preselect current status

        // Save button
        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("save-button");

        saveButton.setOnAction(event -> {
            Ride.Status newStatus = statusComboBox.getValue();

            if (newStatus != null) {
                try {
                    // Mise à jour du statut dans la base de données
                    rideService1.updateRideStatus(ride.getIdRide(), newStatus);

                    // Affichage d'un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Status updated successfully.");
                    successAlert.showAndWait();

                    // Recharger les rides pour refléter le changement
                    loadRidesIntoFlowPane();

                    // Fermeture de la fenêtre après mise à jour
                    dialogStage.close();
                } catch (SQLException e) {
                    // Affichage de l'erreur
                    e.printStackTrace();

                    // Alerte d'erreur
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "An error occurred while updating the status: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            } else {
                // Alerte si aucun statut n'est sélectionné
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a status.");
                alert.showAndWait();
            }
        });

        // Layout
        dialogVBox.getChildren().addAll(new Label("Select New Status:"), statusComboBox, saveButton);

        // Set up scene
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with the main window
        dialogStage.showAndWait();
    }







}