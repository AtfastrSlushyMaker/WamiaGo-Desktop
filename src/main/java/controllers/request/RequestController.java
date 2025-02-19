package controllers.request;

import entities.Location;
import entities.Request;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import services.RequestService;
import services.LocationService;
import services.UserService;
import utils.SessionManager;

import java.io.IOException;
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
    @FXML
    private  Button See_you_Rides_button;

    private final RequestService requestService = new RequestService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm());
        loadRequestsIntoFlowPane();
        setupNavigation();
        request_taxi_button.setOnAction(event -> openRequestForm());
        See_you_Rides_button.setOnAction(event -> loadScene("/taxi-managment/ride.fxml"));
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        // Other navigation buttons commented out.
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
            // Get the logged-in user from session
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();
            int loggedInUserId = user.getId();

            List<Request> requests = requestService.getRequestsByUserId(loggedInUserId);
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
        Button updateButton = createUpdateButton(request, requestCard);

        // Place buttons in an HBox to align them horizontally
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(selectButton, deleteButton, updateButton);

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

        Stage modalStage = new Stage();
        modalStage.setTitle("Request Details - " + request.getIdRequest());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 193, 0.027);");

        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal");
        modalLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Request Details");
        titleLabel.getStyleClass().add("modal-label");

        VBox requestDetailsBox = new VBox(8);
        requestDetailsBox.getStyleClass().add("request-details-box");

        Label arrivalLabel = new Label("Arrival Location: " + request.getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure Location: " + request.getDepartureLocation().getAddress());
        Label statusLabel = new Label("Status: " + request.getStatus());
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());

        arrivalLabel.getStyleClass().add("modal-detail-label");
        departureLabel.getStyleClass().add("modal-detail-label");
        statusLabel.getStyleClass().add("modal-detail-label");
        dateLabel.getStyleClass().add("modal-detail-label");

        requestDetailsBox.getChildren().addAll(arrivalLabel, departureLabel, statusLabel, dateLabel);

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("modal-close-button");
        closeButton.setOnAction(e -> modalStage.close());

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll(titleLabel, requestDetailsBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 350, 250);
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private Button createSelectButton(Request request) {
        Button selectButton = new Button("Details");
        selectButton.getStyleClass().add("request-button");
        selectButton.setOnAction(e -> openRequestDetails(request));
        return selectButton;
    }

    private Button createDeleteButton(Request request, VBox requestCard) {
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("request-button-delete");

        deleteButton.setOnAction(event -> {
            try {
                requestService.delete(request.getIdRequest());
                requestFlowPane.getChildren().remove(requestCard);
                System.out.println("Request ID " + request.getIdRequest() + " deleted.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to delete request ID " + request.getIdRequest());
            }
        });

        return deleteButton;
    }

    private Button createUpdateButton(Request request, VBox requestCard) {
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("request-button-update");

        updateButton.setOnAction(event -> {
            openUpdateRequestModal(request, requestCard);
        });

        return updateButton;
    }

    private void openUpdateRequestModal(Request request, VBox requestCard) {
        // Create a new Stage (popup/modal)
        Stage modalStage = new Stage();
        modalStage.setTitle("Update Request Locations - " + request.getIdRequest());

        // Create the stack pane for the dark overlay with transparency
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(255, 255, 193, 0.027);");

        // Modal layout container
        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal");
        modalLayout.setPadding(new Insets(20));

        // Title label for the modal
        Label titleLabel = new Label("Update Request Locations");
        titleLabel.getStyleClass().add("modal-label");

        // Create ComboBoxes for updating departure and arrival locations
        ComboBox<Location> departureComboBox = new ComboBox<>();
        ComboBox<Location> arrivalComboBox = new ComboBox<>();

        // Populate the ComboBoxes with all available locations
        LocationService locationService = new LocationService();
        try {
            List<Location> locations = locationService.read();
            departureComboBox.getItems().setAll(locations);
            arrivalComboBox.getItems().setAll(locations);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the current locations as default selections
        departureComboBox.setValue(request.getDepartureLocation());
        arrivalComboBox.setValue(request.getArrivalLocation());

        // Create an "Update" button
        Button updateRequestButton = new Button("Update Locations");
        updateRequestButton.setOnAction(event -> {
            // Retrieve the new locations
            Location newDeparture = departureComboBox.getValue();
            Location newArrival = arrivalComboBox.getValue();

            if(newDeparture != null && newArrival != null) {
                // Update the request with new locations
                request.setDepartureLocation(newDeparture);
                request.setArrivalLocation(newArrival);

                try {
                    // Call service method to update the request locations in the database
                    // (Ensure your update method handles updating only the location columns)
                    requestService.update(request);
                    System.out.println("✅ Request locations updated successfully!");

                    // Close the modal after update
                    modalStage.close();

                    // Refresh the request in the FlowPane to reflect the changes
                    refreshRequestsFlowPane();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Please select both departure and arrival locations.");
            }
        });

        // Add all components to the modal layout
        modalLayout.getChildren().addAll(titleLabel,
                new Label("Departure Location:"), departureComboBox,
                new Label("Arrival Location:"), arrivalComboBox,
                updateRequestButton);

        // Add the modal layout to the stack pane
        stackPane.getChildren().add(modalLayout);

        // Set up the Scene and Stage
        Scene modalScene = new Scene(stackPane, 350, 300);
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }


    private void openRequestForm() {
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Taxi");

        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal");
        modalLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Request Taxi");
        titleLabel.getStyleClass().add("modal-label");

        ComboBox<Location> departureComboBox = new ComboBox<>();
        ComboBox<Location> arrivalComboBox = new ComboBox<>();

        LocationService locationService = new LocationService();
        try {
            List<Location> locations = locationService.read();
            departureComboBox.getItems().setAll(locations);
            arrivalComboBox.getItems().setAll(locations);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VBox comboBoxBox = new VBox(10);
        comboBoxBox.getChildren().addAll(
                new Label("Departure Location:"), departureComboBox,
                new Label("Arrival Location:"), arrivalComboBox
        );

        Button requestTaxiButton = new Button("Request Taxi");
        requestTaxiButton.setOnAction(event -> {
            Location departureLocation = departureComboBox.getValue();
            Location arrivalLocation = arrivalComboBox.getValue();

            if (departureLocation != null && arrivalLocation != null) {
                Request newRequest = new Request();
                newRequest.setDepartureLocation(departureLocation);
                newRequest.setArrivalLocation(arrivalLocation);
                newRequest.setStatus(Request.RequestStatus.PENDING);
                newRequest.setRequestDate(LocalDateTime.now());

                SessionManager sessionManager = SessionManager.getInstance();
                User user = sessionManager.getUser();
                int loggedInUserId = user.getId();

                UserService clientService = new UserService();
                try {
                    User client = clientService.getById(loggedInUserId);
                    newRequest.setClient(client);

                    requestService.create(newRequest);
                    System.out.println("✅ Request created successfully!");
                    // Refresh requests using session-based method
                    refreshRequestsFlowPane();

                    modalStage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Please select both departure and arrival locations.");
            }
        });

        modalLayout.getChildren().addAll(titleLabel, comboBoxBox, requestTaxiButton);
        Scene modalScene = new Scene(modalLayout, 350, 250);
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/request.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void refreshRequestsFlowPane() {
        try {
            // Retrieve logged-in user from session
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();
            int loggedInUserId = user.getId();

            requestFlowPane.getChildren().clear();

            List<Request> updatedRequests = requestService.getRequestsByUserId(loggedInUserId);
            for (Request request : updatedRequests) {
                VBox requestCard = createRequestCard(request);
                requestFlowPane.getChildren().add(requestCard);
            }
            System.out.println("🔄 Requests refreshed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
