package controllers.request;

import entities.Location;
import entities.Request;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RequestController {

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
    private FlowPane requestFlowPane;

    @FXML
    private Button request_taxi_button;


    private final RequestService requestService = new RequestService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm());
        loadRequestsIntoFlowPane();

        setupNavigation();
        URL cssURL = getClass().getResource("/taxi-managment/request.css");
        if (cssURL == null) {
            System.out.println("CSS file not found!");
        } else {
            System.out.println("CSS file found: " + cssURL.toExternalForm());
        }
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        // bookings_button.setOnAction(event -> loadScene("/bookings/bookings.fxml"));
        // history_button.setOnAction(event -> loadScene("/history/history.fxml"));
        // logout_button.setOnAction(event -> logout());
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

    private void loadRequestsIntoFlowPane() {
        try {
            List<Request> requests =  requestService.getRequestsByUserId(3);
            for (Request request : requests) {
                System.out.println("Request ID: " + request.getIdRequest());
                System.out.println("Arrival Location: " + request.getArrivalLocation().getAddress());
                System.out.println("Departure Location: " + request.getDepartureLocation().getAddress());
                System.out.println("Status: " + request.getStatus());
                VBox requestCard = createRequestCard(request);
                requestFlowPane.getChildren().add(requestCard);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createRequestCard(Request request) {
        VBox requestCard = new VBox(10);
        requestCard.setPadding(new Insets(10));
        requestCard.getStyleClass().add("request-card");
        requestCard.setAlignment(Pos.CENTER);

        // Create image and text box for request status
        HBox imageAndTextBox = createImageAndTextBox(request);
        requestCard.getChildren().add(imageAndTextBox);

        // Show only the request date
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        requestCard.getChildren().add(dateLabel);

        // Create buttons
        Button selectButton = createSelectButton(request);
        Button deleteButton = createDeleteButton(request, requestCard);

        // Place buttons in an HBox to align them horizontally
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(selectButton, deleteButton);

        requestCard.getChildren().add(buttonContainer); // Add the HBox to the VBox

        // Add hover effect for the request card
        requestCard.setOnMouseExited(event -> {
            requestCard.setScaleX(1);
            requestCard.setScaleY(1);
        });

        requestCard.setOnMouseEntered(event -> {
            requestCard.setScaleX(1.05);
            requestCard.setScaleY(1.05);
        });

        return requestCard;
    }



    private HBox createImageAndTextBox(Request request) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setFillHeight(true);

        ImageView requestImage = new ImageView(new Image(getClass().getResource("/images/icons/taxi.png").toExternalForm()));
        requestImage.setFitWidth(50);
        requestImage.setFitHeight(50);
        requestImage.setPreserveRatio(true);

        Text statusText = new Text("Status: " + request.getStatus());
        statusText.setWrappingWidth(180);
        statusText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(statusText, Priority.ALWAYS);
        hbox.getChildren().addAll(requestImage, statusText);

        return hbox;
    }
    private void openRequestDetails(Request request) {
        System.out.println("Opening details for Request ID: " + request.getIdRequest());

        // Create a new Stage (popup/modal)
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Details - " + request.getIdRequest());

        // Create the stack pane for the dark overlay with transparency
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 193, 0.027);"); // Semi-transparent yellow overlay


        // Modal layout container
        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal"); // Add custom modal style from CSS
        modalLayout.setPadding(new Insets(20)); // Padding around the modal

        // Title label for the modal
        Label titleLabel = new Label("Request Details");
        titleLabel.getStyleClass().add("modal-label"); // Apply modal label style from CSS

        // Create a VBox to hold the request details
        VBox requestDetailsBox = new VBox(8);
        requestDetailsBox.getStyleClass().add("request-details-box"); // Apply custom details box style

        // Request details labels
        Label arrivalLabel = new Label("Arrival Location: " + request.getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure Location: " + request.getDepartureLocation().getAddress());
        Label statusLabel = new Label("Status: " + request.getStatus());
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());

        // Add labels to the requestDetailsBox with custom styling
        arrivalLabel.getStyleClass().add("modal-detail-label");
        departureLabel.getStyleClass().add("modal-detail-label");
        statusLabel.getStyleClass().add("modal-detail-label");
        dateLabel.getStyleClass().add("modal-detail-label");

        requestDetailsBox.getChildren().addAll(arrivalLabel, departureLabel, statusLabel, dateLabel);

        // Close button with style
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("modal-close-button"); // Apply close button style
        closeButton.setOnAction(e -> modalStage.close());

        // Close button container (HBox for centering)
        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        // Add all components to the modal layout
        modalLayout.getChildren().addAll(titleLabel, requestDetailsBox, closeButtonContainer);

        // Add the modal layout to the stack pane
        stackPane.getChildren().add(modalLayout);

        // Set up the Scene and Stage
        Scene modalScene = new Scene(stackPane, 350, 250); // Adjust the size as needed
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm()); // Ensure correct path to your CSS file
        modalStage.setScene(modalScene);
        modalStage.show();
    }



    private Button createSelectButton(Request request) {
        Button selectButton = new Button("Details");
        selectButton.getStyleClass().add("request-button"); // Optional: use a specific CSS class for request buttons
        selectButton.setOnAction(e -> openRequestDetails(request)); // Open request details when clicked
        return selectButton;
    }

    private Button createDeleteButton(Request request, VBox requestCard) {
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("request-button-delete"); // Optional: Apply a CSS class for styling

        deleteButton.setOnAction(event -> {
            try {
                requestService.delete(request.getIdRequest()); // Delete from the database
                requestFlowPane.getChildren().remove(requestCard); // Remove from the UI
                System.out.println("Request ID " + request.getIdRequest() + " deleted.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to delete request ID " + request.getIdRequest());
            }
        });

        return deleteButton;
    }


    private void openRequestForm() {
        // Create a new Stage (popup/modal)
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Taxi");

        // Create the stack pane for the dark overlay with transparency
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 193, 0.027);"); // Semi-transparent yellow overlay

        // Modal layout container
        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal"); // Add custom modal style from CSS
        modalLayout.setPadding(new Insets(20)); // Padding around the modal

        // Title label for the modal
        Label titleLabel = new Label("Request Taxi");
        titleLabel.getStyleClass().add("modal-label"); // Apply modal label style from CSS

        // Create the ComboBoxes for departure and arrival locations
        ComboBox<Location> departureComboBox = new ComboBox<>();
        ComboBox<Location> arrivalComboBox = new ComboBox<>();

        // Get all locations from the database using the LocationService
        LocationService locationService = new LocationService();
        try {
            List<Location> locations = locationService.read(); // Get all locations
            departureComboBox.getItems().setAll(locations); // Populate departure locations
            arrivalComboBox.getItems().setAll(locations); // Populate arrival locations
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create a VBox to hold the ComboBoxes and labels
        VBox comboBoxBox = new VBox(10);
        comboBoxBox.getChildren().addAll(
                new Label("Departure Location:"), departureComboBox,
                new Label("Arrival Location:"), arrivalComboBox
        );

        // Create the 'Request Taxi' button
        Button requestTaxiButton = new Button("Request Taxi");
        requestTaxiButton.setOnAction(event -> {
            Location departureLocation = departureComboBox.getValue();
            Location arrivalLocation = arrivalComboBox.getValue();

            if (departureLocation != null && arrivalLocation != null) {
                // Set the status to PENDING by default
                Request newRequest = new Request();
                newRequest.setDepartureLocation(departureLocation);
                newRequest.setArrivalLocation(arrivalLocation);
                newRequest.setStatus(Request.RequestStatus.PENDING);  // Using enum

                // Set the request date as the current timestamp
                newRequest.setRequestDate(LocalDateTime.now());

                try {
                    // Call the service method to create the request in the database
                    requestService.create(newRequest);
                    System.out.println("Request created successfully!");
                    modalStage.close(); // Close the modal after request creation
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Please select both departure and arrival locations.");
            }
        });

        // Add all components to the modal layout
        modalLayout.getChildren().addAll(titleLabel, comboBoxBox, requestTaxiButton);

        // Add the modal layout to the stack pane
        stackPane.getChildren().add(modalLayout);

        // Set up the Scene and Stage
        Scene modalScene = new Scene(stackPane, 350, 250); // Adjust the size as needed
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm()); // Ensure correct path to your CSS file
        modalStage.setScene(modalScene);
        modalStage.show();
    }








}
