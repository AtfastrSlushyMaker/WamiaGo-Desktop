package controllers.request;

import entities.Request;
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
import services.RequestService;

import java.io.IOException;
import java.net.URL;
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
            List<Request> requests = requestService.read();
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

        // Create image and text box for request name
        HBox imageAndTextBox = createImageAndTextBox(request);
        requestCard.getChildren().add(imageAndTextBox);

        // Add additional information (e.g., ID, arrival, departure, status)
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label arrivalLabel = new Label("Arrival: " + request.getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure: " + request.getDepartureLocation().getAddress());
        Label statusLabel = new Label("Status: " + request.getStatus());

        arrivalLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        departureLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        infoBox.getChildren().addAll(arrivalLabel, departureLabel, statusLabel);
        requestCard.getChildren().add(infoBox);

        // Add "Select" button for the request card
        Button selectButton = createSelectButton(request);
        requestCard.getChildren().add(selectButton);

        // Add event for mouse hover effect
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

        Text nameText = new Text(request.getClient().getName());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(requestImage, nameText);

        return hbox;
    }

    private void openRequestDetails(Request request) {
        System.out.println("Opening details for Request ID: " + request.getIdRequest());

        // Create a new Stage (popup/modal)
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Details - " + request.getIdRequest());

        // Dark overlay with some transparency
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Semi-transparent black overlay

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);"); // Dark background and shadow for modal

        // Title for the modal
        Label titleLabel = new Label("Request Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;-fx-text-alignment: center;-fx-font-family: Inter");

        // Display request details in the modal
        VBox requestDetailsBox = new VBox(8);

        // Add only selected fields (Arrival and Departure Locations, Status, Date)
        Label arrivalLabel = new Label("Arrival Location: " + request.getArrivalLocation().getAddress());
        Label departureLabel = new Label("Departure Location: " + request.getDepartureLocation().getAddress());
        Label statusLabel = new Label("Status: " + request.getStatus());
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());

        // Apply styles to labels
        arrivalLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        departureLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");

        // Add the selected labels to the VBox
        requestDetailsBox.getChildren().addAll(arrivalLabel, departureLabel, statusLabel, dateLabel);

        // Close button for the modal
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("request-details-close-button");

        // Create an HBox to center the button at the bottom
        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);  // Center the button in the HBox
        closeButtonContainer.getChildren().add(closeButton);

        // Add components to the VBox
        modalLayout.getChildren().addAll(titleLabel, requestDetailsBox, closeButtonContainer);

        stackPane.getChildren().add(modalLayout);  // Add the modal layout to the stack pane

        // Set up the Scene and Stage
        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private Button createSelectButton(Request request) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("request-button"); // Optional: use a specific CSS class for request buttons
        selectButton.setOnAction(e -> openRequestDetails(request)); // Open request details when clicked
        return selectButton;
    }
}
