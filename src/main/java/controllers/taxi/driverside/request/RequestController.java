package controllers.taxi.driverside.request;

import entities.Driver;
import entities.Request;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

import java.io.IOException;
import java.sql.SQLException;
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
    private Button See_you_Rides_button;

    private final RequestService requestService = new RequestService();
    private final UserService userService = new UserService();

    private Driver currentDriver; // Chauffeur utilisé pour tester

    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/driver_side/request.css").toExternalForm());



        UserService userService = new UserService();
        DriverService driverService = new DriverService();

        try {
            // Assuming you get the logged-in user (this could be from session or context)
            User loggedInUser = userService.getById(2);  // Example: fetch user with ID = 2

            // Check if the user has an associated driver
            Driver currentDriver = driverService.getById(loggedInUser.getId());  // Adjust based on how you get a driver for the user

            if (currentDriver != null) {
                // The user is also a driver, so we can proceed to driver-specific logic
                System.out.println("User is also a driver. Initializing driver-specific logic.");
                loadRequestsIntoFlowPane();
                setupNavigation();
            } else {
                // The user does not have a driver role, handle it as a regular user
                System.out.println("User is not a driver.");
                // You can display a message or navigate to a regular user interface
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération du chauffeur : " + e.getMessage());
            e.printStackTrace();
        }

        loadRequestsIntoFlowPane();
        setupNavigation();
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
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
            List<Request> requests = requestService.read(); // Récupérer toutes les demandes

            requestFlowPane.getChildren().clear(); // Nettoyer avant d'ajouter de nouvelles cartes

            for (Request request : requests) {
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

        // Crée la boîte avec l'image et le texte du statut
        HBox imageAndTextBox = createImageAndTextBox(request);
        requestCard.getChildren().add(imageAndTextBox);

        // Création des labels de date et autres détails
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        requestCard.getChildren().add(dateLabel);

        // Création des boutons
        Button acceptButton = createAcceptButton(request);
        Button selectButton = createSelectButton(request);  // Ajout du bouton "Details"

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(acceptButton, selectButton);  // Ajout du bouton "Details" dans la même ligne

        requestCard.getChildren().add(buttonContainer);

        // Effet de survol
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

    private Button createAcceptButton(Request request) {
        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("request-button");
        acceptButton.setOnAction(event -> {
            try {
                // Utiliser le type correct de currentDriver, sans le caster
                requestService.acceptRequest(request, currentDriver);  // Passer un vrai chauffeur
                loadRequestsIntoFlowPane();  // Rafraîchir après acceptation
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return acceptButton;
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
}
