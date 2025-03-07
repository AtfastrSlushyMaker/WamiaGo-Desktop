package controllers.taxi.userside.request;

import entities.Location;
import entities.Request;
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
import services.UserService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestController {

    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private DatePicker searchDatePicker;


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
        root.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/request.css").toExternalForm());
        loadRequestsIntoFlowPane();
        setupNavigation();
        searchDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> filterRequestsByDate(newValue));
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterRequests(newValue));
        request_taxi_button.setOnAction(event -> openRequestForm());
        See_you_Rides_button.setOnAction(event -> loadScene("/taxi-managment/user_side/ride.fxml"));
    }


    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        // Other navigation buttons commented out.
    }

    private void filterRequestsByDate(LocalDate selectedDate) {
        if (selectedDate != null) {
            try {
                // Récupérer l'utilisateur connecté
                SessionManager sessionManager = SessionManager.getInstance();
                User user = sessionManager.getUser();
                int loggedInUserId = user.getId();

                // Récupérer toutes les demandes pour l'utilisateur connecté
                List<Request> requests = requestService.getRequestsByUserId(loggedInUserId);

                // Filtrer les demandes en fonction de la date sélectionnée
                List<Request> filteredRequests = requests.stream()
                        .filter(request -> request.getRequestDate().toLocalDate().isEqual(selectedDate))
                        .collect(Collectors.toList());

                // Rafraîchir la vue avec les demandes filtrées
                updateRequestFlowPane(filteredRequests);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void filterRequests(String searchText) {
        try {
            // Récupérer l'utilisateur connecté
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();
            int loggedInUserId = user.getId();

            // Récupérer toutes les demandes pour l'utilisateur connecté
            List<Request> requests = requestService.getRequestsByUserId(loggedInUserId);

            // Filtrer les demandes en fonction du texte de recherche
            List<Request> filteredRequests = requests.stream()
                    .filter(request -> request.getDepartureLocation().getAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                            request.getArrivalLocation().getAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                            request.getStatus().toString().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());

            // Rafraîchir la vue avec les demandes filtrées
            updateRequestFlowPane(filteredRequests);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateRequestFlowPane(List<Request> filteredRequests) {
        // Vider le FlowPane avant d'ajouter les nouvelles demandes
        requestFlowPane.getChildren().clear();

        // Ajouter les demandes filtrées au FlowPane
        for (Request request : filteredRequests) {
            if (request.getStatus() != Request.RequestStatus.ACCEPTED) {
                VBox requestCard = createRequestCard(request);
                requestFlowPane.getChildren().add(requestCard);
            }
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

    private void loadRequestsIntoFlowPane() {
        try {
            // Get the logged-in user from session
            SessionManager sessionManager = SessionManager.getInstance();
            User user = sessionManager.getUser();
            int loggedInUserId = user.getId();

            // Retrieve all requests for the logged-in user
            List<Request> requests = requestService.getRequestsByUserId(loggedInUserId);

            // Clear the FlowPane before adding new requests
            requestFlowPane.getChildren().clear();

            // Loop through the requests and filter out those with status "ACCEPTED"
            for (Request request : requests) {
                if (request.getStatus() != Request.RequestStatus.ACCEPTED) {
                    // Only add requests that are not ACCEPTED
                    VBox requestCard = createRequestCard(request);
                    requestFlowPane.getChildren().add(requestCard);
                }
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
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/request.css").toExternalForm());
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
            // Create the confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this request?");
            alert.setContentText("This action cannot be undone.");

            // Wait for user response
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    requestService.delete(request.getIdRequest());
                    requestFlowPane.getChildren().remove(requestCard);
                    System.out.println("Request ID " + request.getIdRequest() + " deleted.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Failed to delete request ID " + request.getIdRequest());
                }
            } else {
                System.out.println("Deletion canceled.");
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

        // Configurer l'affichage des noms des locations dans les ComboBox
        departureComboBox.setCellFactory(lv -> new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });
        departureComboBox.setButtonCell(new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });

        arrivalComboBox.setCellFactory(lv -> new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });
        arrivalComboBox.setButtonCell(new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });

        // Create an "Update" button
        Button updateRequestButton = new Button("Update Locations");
        updateRequestButton.setOnAction(event -> {
            // Retrieve the new locations
            Location newDeparture = departureComboBox.getValue();
            Location newArrival = arrivalComboBox.getValue();

            if (newDeparture != null && newArrival != null) {
                // Update the request with new locations
                request.setDepartureLocation(newDeparture);
                request.setArrivalLocation(newArrival);

                try {
                    // Call service method to update the request locations in the database
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

        // Configurer l'affichage pour afficher uniquement le nom de la location
        departureComboBox.setCellFactory(lv -> new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });
        departureComboBox.setButtonCell(new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });

        arrivalComboBox.setCellFactory(lv -> new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });
        arrivalComboBox.setButtonCell(new ListCell<Location>() {
            @Override
            protected void updateItem(Location item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getAddress());
            }
        });

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

                    // Rafraîchir les requêtes
                    loadRequestsIntoFlowPane();

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
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/request.css").toExternalForm());
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
