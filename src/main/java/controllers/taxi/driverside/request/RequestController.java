package controllers.taxi.driverside.request;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import entities.Driver;
import entities.Request;
import entities.User;
import entities.Ride;
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
import services.RideService;
import utils.SessionManager;
import javafx.scene.control.TextFormatter;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    private Button See_you_Rides_button;

    private final RequestService requestService = new RequestService();
    private final UserService userService = new UserService();
    private final DriverService driverService = new DriverService();
    private final RideService rideService = new RideService();

    private static final String ACCOUNT_SID = "AC9012091b8b155a0743d30e8f6cbdbe55";
    private static final String AUTH_TOKEN = "6dafad2d58c6d83e299d629d032aa219";
    private static final String TWILIO_PHONE_NUMBER = "+12402554395"; // Numéro Twilio

    // Class-level field to hold the current driver.
    private Driver currentDriver;

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/driver_side/request.css").toExternalForm());

        try {
            // Récupérer l'utilisateur connecté. (Pour les tests, utiliser un ID codé en dur)
            User loggedInUser = SessionManager.getInstance().getUser();

            // Récupérer le chauffeur en utilisant l'ID de l'utilisateur connecté.
            currentDriver = driverService.getById(loggedInUser.getId());

            // Ajouter un test pour afficher l'ID du chauffeur connecté
            if (currentDriver != null) {
                System.out.println("User is also a driver. Driver ID: " + currentDriver.getIdDriver());  // Afficher l'ID du chauffeur
                loadRequestsIntoFlowPane();
                setupNavigation();
            } else {
                System.out.println("User is not a driver.");
                // Optionnellement gérer les utilisateurs non chauffeurs.
            }
        } catch (SQLException e) {
            System.err.println("SQL error while retrieving the driver: " + e.getMessage());
            e.printStackTrace();
        }

        See_you_Rides_button.setOnAction(event -> loadScene("/taxi-managment/driver_side/ride.fxml"));
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        See_you_Rides_button.setOnAction(event -> loadScene("/ride.fxml"));
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

    private void loadRequestsIntoFlowPane() {
        try {
            List<Request> requests = requestService.read(); // Retrieve all requests
            requestFlowPane.getChildren().clear(); // Clear existing cards

            for (Request request : requests) {
                // Only display requests that are not accepted.
                if (request.getStatus() != Request.RequestStatus.ACCEPTED) {
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

        HBox imageAndTextBox = createImageAndTextBox(request);
        requestCard.getChildren().add(imageAndTextBox);

        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        requestCard.getChildren().add(dateLabel);

        // Create buttons: Accept and Details.
        Button acceptButton = createAcceptButton(request);
        Button selectButton = createSelectButton(request);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(acceptButton, selectButton);
        requestCard.getChildren().add(buttonContainer);

        // Hover effects.
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

    // Accept button: opens the duration modal if the request is not already accepted.
    private Button createAcceptButton(Request request) {
        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("request-button");

        // Disable the button if the request is already accepted.
        if (request.getStatus() == Request.RequestStatus.ACCEPTED) {
            acceptButton.setDisable(true);
        }
        acceptButton.setOnAction(event -> {
            if (request.getStatus() != Request.RequestStatus.ACCEPTED) {
                openDurationModal(request);
            } else {
                System.out.println("Request already accepted.");
            }
        });
        return acceptButton;
    }

    private Button createSelectButton(Request request) {
        Button selectButton = new Button("Details");
        selectButton.getStyleClass().add("request-button");
        selectButton.setOnAction(e -> openRequestDetails(request));
        return selectButton;
    }

    private void openRequestDetails(Request request) {
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

        Label clientLabel = new Label("Client: " + (request.getClient() != null ? request.getClient().getName() : "Unknown"));
        Label arrivalLabel = new Label("Arrival Location: " + request.getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure Location: " + request.getDepartureLocation().getAddress());
        Label statusLabel = new Label("Status: " + request.getStatus());
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());

        clientLabel.getStyleClass().add("modal-detail-label");
        arrivalLabel.getStyleClass().add("modal-detail-label");
        departureLabel.getStyleClass().add("modal-detail-label");
        statusLabel.getStyleClass().add("modal-detail-label");
        dateLabel.getStyleClass().add("modal-detail-label");

        requestDetailsBox.getChildren().addAll(clientLabel, arrivalLabel, departureLabel, statusLabel, dateLabel);

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


    private void openDurationModal(Request request) {
        Stage durationStage = new Stage();
        durationStage.setTitle("Enter Ride Duration");
        RideService rideService = new RideService(); // Utilisation de la bonne instance de service

        VBox layout = new VBox(10);
        layout.getStyleClass().add("modal-container");
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label label = new Label("Enter ride duration (in minutes):");
        label.getStyleClass().add("modal-label");

        TextField durationField = new TextField();
        durationField.setPromptText("Duration in minutes");
        durationField.getStyleClass().add("duration-field");

        // Appliquer un filtre pour empêcher la saisie de caractères non numériques
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        });
        durationField.setTextFormatter(textFormatter);

        Label warningLabel = new Label();
        warningLabel.getStyleClass().add("warning-label");
        warningLabel.setVisible(false);

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("submit-button");

        submitButton.setOnAction(e -> {
            try {
                String durationText = durationField.getText().trim();
                if (durationText.isEmpty()) {
                    warningLabel.setText("⚠️ Please enter a duration.");
                    warningLabel.setVisible(true);
                    return;
                }

                int duration = Integer.parseInt(durationText);
                if (duration < 1 || duration > 300) {
                    warningLabel.setText("⚠️ Duration must be between 1 and 300 minutes.");
                    warningLabel.setVisible(true);
                    return;
                }

                warningLabel.setVisible(false);
                double price = RideService.calculatePrice(request);

                // Création de la course
                Ride newRide = new Ride();
                newRide.setRequest(request);
                newRide.setDriver(currentDriver);
                newRide.setStatus(Ride.Status.ONGOING);
                newRide.setRideDate(new Timestamp(System.currentTimeMillis()));
                newRide.setDuration(duration);
                newRide.setPrice(price);

                boolean created = rideService.create(newRide);
                if (created) {
                    System.out.println("✅ Ride created successfully");
                    request.setStatus(Request.RequestStatus.ACCEPTED);
                    requestService.update(request);

                    // Envoi du SMS au client
                    String clientPhoneNumber = request.getClient().getPhone();
                    sendSmsToClient(clientPhoneNumber, duration, price);

                } else {
                    System.out.println("❌ Ride creation failed");
                }

                durationStage.close();
                loadRequestsIntoFlowPane();

            } catch (NumberFormatException ex) {
                warningLabel.setText("⚠️ Invalid duration. Please enter a valid number.");
                warningLabel.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace(); // Pour capturer toute autre exception inattendue
            }
        });

        layout.getChildren().addAll(label, durationField, warningLabel, submitButton);
        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/taxi-managment/driver_side/duration-modal.css").toExternalForm());
        durationStage.setScene(scene);
        durationStage.show();
    }

    private void sendSmsToClient(String clientPhoneNumber, int duration, double price) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Formater le prix pour afficher 3 chiffres après la virgule
            String formattedPrice = String.format("%.3f", price);

            String messageBody = "Your ride has been accepted! Estimated duration: "
                    + duration + " minutes. Price: " + formattedPrice + " DT.";

            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(clientPhoneNumber),
                    new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                    messageBody
            ).create();

            System.out.println("✅ SMS sent to client: " + clientPhoneNumber);
        } catch (Exception e) {
            System.out.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }



}
