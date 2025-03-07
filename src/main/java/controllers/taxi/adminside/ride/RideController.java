package controllers.taxi.adminside.ride;

import entities.Ride;
import entities.Request;
import entities.User;
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
import services.RideService;  // Import your RideService

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RideController {

    @FXML
    private Button home_button;
    @FXML
    private Button logout_button;
    @FXML
    private FlowPane rideFlowPane;
    @FXML
    private Button back_button;
    @FXML
    private AnchorPane root;

    private final RideService rideService = new RideService();

    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/taxi-managment/admin_side/ride.css").toExternalForm());
        loadAllRidesIntoFlowPane();
        //updateTotalRequests(); // Call the method to update the total request count
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
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

    private void handleBack() {

    }

    private void loadAllRidesIntoFlowPane() {
        try {
            List<Ride> rides = rideService.read();
            rideFlowPane.getChildren().clear();

            for (Ride ride : rides) {
                VBox rideCard = createRideCard(ride);
                rideFlowPane.getChildren().add(rideCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load the rides from the database.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private VBox createRideCard(Ride ride) {
        VBox rideCard = new VBox(5);
        rideCard.setPadding(new Insets(5));
        rideCard.getStyleClass().add("ride-card");
        rideCard.setAlignment(Pos.CENTER);
        rideCard.setPrefWidth(250);

        HBox imageAndTextBox = createImageAndTextBoxForRide(ride);
        rideCard.getChildren().add(imageAndTextBox);

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

        Button selectButton = createSelectButtonForRide(ride);
        Button cancelButton = createCancelButton(ride, rideCard);
        Button updateButton = createUpdateButton(ride);

        selectButton.setPrefSize(80, 30);
        cancelButton.setPrefSize(80, 30);
        updateButton.setPrefSize(80, 30);

        HBox buttonContainer = new HBox(5, selectButton, cancelButton, updateButton);
        buttonContainer.setAlignment(Pos.CENTER);

        rideCard.getChildren().add(buttonContainer);

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

        ImageView rideImage = new ImageView(new Image(getClass().getResource("/images/icons/taxi.png").toExternalForm()));
        rideImage.setFitWidth(50);
        rideImage.setFitHeight(50);
        rideImage.setPreserveRatio(true);

        Text statusText = new Text("Status: " + ride.getStatus());
        statusText.setWrappingWidth(180);
        statusText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(statusText, Priority.ALWAYS);
        hbox.getChildren().addAll(rideImage, statusText);

        return hbox;
    }

    private Button createSelectButtonForRide(Ride ride) {
        Button selectButton = new Button("Details");
        selectButton.getStyleClass().add("ride-button");
        selectButton.setOnAction(e -> openRideDetails(ride));
        return selectButton;
    }

    private Button createCancelButton(Ride ride, VBox rideCard) {
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("ride-button-delete");

        deleteButton.setOnAction(event -> {
            try {
                rideService.delete(ride.getIdRide());
                rideFlowPane.getChildren().remove(rideCard);
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

    private void openRideDetails(Ride ride) {
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

        Label arrivalLabel = new Label("Arrival Location: " + (ride.getRequest().getArrivalLocation() != null ? ride.getRequest().getArrivalLocation().getAddress() : "Unknown"));
        Label departureLabel = new Label("Departure Location: " + (ride.getRequest().getDepartureLocation() != null ? ride.getRequest().getDepartureLocation().getAddress() : "Unknown"));

        Label distanceLabel = new Label("Distance: " + ride.getDistance() + " km");
        Label durationLabel = new Label("Duration: " + ride.getDuration() + " min");
        Label priceLabel = new Label("Price: " + ride.getPrice() + " TND");
        Label statusLabel = new Label("Status: " + ride.getStatus());
        Label dateLabel = new Label("Date: " + ride.getRideDate().toString());
        Label clientLabel = new Label("Client Name: " + ride.getRequest().getClient().getName());

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
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/admin_side/ride.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void openUpdateStatusDialog(Ride ride) {
        RideService rideService1 = new RideService();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Update Ride Status");

        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        ComboBox<Ride.Status> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Ride.Status.values());
        statusComboBox.setValue(ride.getStatus());

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("save-button");

        saveButton.setOnAction(event -> {
            Ride.Status newStatus = statusComboBox.getValue();

            if (newStatus != null) {
                try {
                    rideService1.updateRideStatus(ride.getIdRide(), newStatus);
                    loadAllRidesIntoFlowPane();  // Reload the rides list
                    dialogStage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a status.");
                alert.showAndWait();
            }
        });

        dialogVBox.getChildren().addAll(new Label("Select New Status:"), statusComboBox, saveButton);

        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
    }
}
