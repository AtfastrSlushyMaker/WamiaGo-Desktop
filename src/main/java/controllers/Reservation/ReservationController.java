package controllers.Reservation;

import entities.Relocation;
import entities.Reservation;
import entities.Station;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ReservationService;
import services.StationService;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import services.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReservationController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private AnchorPane root;
    @FXML
    private FlowPane stationFlowPane;

    private final ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
        loadStationsIntoFlowPane();
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

    private void loadStationsIntoFlowPane() {
        try {
            for (Reservation station : reservationService.read()) {
                VBox stationCard = createStationCard(station);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Reservation station) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(10));
        stationCard.setAlignment(Pos.CENTER);

        stationCard.setStyle("-fx-background-color: #f0f8ff; " +
                "-fx-background-radius: 45px; " +
                "-fx-border-radius: 45px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5); " +
                "-fx-padding: 10px; " +
                "-fx-pref-width: 350px; " +
                "-fx-cursor: hand; " +
                "-fx-transition: all 0.3s;");

        HBox imageAndTextBox = createImageAndTextBox(station);

        Label localDate = new Label("Date: " + station.getDate());
        Label status = new Label("Status: " + station.getStatus());
        Label description = new Label("Description: " + station.getDescription());
        Label startLocation = new Label("Start: " + station.getStartLocation().getAddress());
        Label endLocation = new Label("End: " + station.getEndLocation().getAddress());

        // Create the buttons
        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> openStationDetails(station));

//        Button editButton = new Button("Edit");
//        editButton.setOnAction(e -> editReservation(station));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteReservation(station));

        selectButton.getStyleClass().add("station-button-admin");
       // editButton.getStyleClass().add("station-button-admin");
        deleteButton.getStyleClass().add("station-button-admin");

        // Create a single HBox for the buttons
        HBox buttonBox = new HBox(10, selectButton,  deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add components to the station card
        stationCard.getChildren().addAll(imageAndTextBox, localDate, status, description, startLocation, endLocation, buttonBox);

        stationCard.setOnMouseEntered(event -> {
            stationCard.setScaleX(1.05);
            stationCard.setScaleY(1.05);
        });

        stationCard.setOnMouseExited(event -> {
            stationCard.setScaleX(1);
            stationCard.setScaleY(1);
        });

        return stationCard;
    }

    private HBox createImageAndTextBox(Reservation station) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER); // Center the HBox

        // Create a VBox for the icon and title
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER); // Center the VBox
        vbox.setSpacing(5); // Add some spacing between icon and title

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/date.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        // Use TextFlow to center the text
        TextFlow nameTextFlow = new TextFlow();
        Text nameText = new Text(station.getAnnouncement().getTitle());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #3a3a3a;");

        nameTextFlow.getChildren().add(nameText);
        nameTextFlow.setTextAlignment(TextAlignment.CENTER); // Center the text in the TextFlow

        // Add the ImageView and TextFlow to the VBox
        vbox.getChildren().addAll(stationImage, nameTextFlow);

        // Add the VBox to the HBox
        hbox.getChildren().add(vbox);

        return hbox;
    }


    private void openStationDetails(Reservation reservation) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Détails");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);");

        Label title = new Label(reservation.getAnnouncement().getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Load icons
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(reservation.getStatus().equals("Completed") ?
                "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView descriptionIcon = createIcon("/images/icons/description.png");
        ImageView startIcon = createIcon("/images/icons/place.png");
        ImageView endIcon = createIcon("/images/icons/place.png");

        // Create HBox for labels with icons
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + reservation.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + reservation.getStatus());
        HBox descriptionBox = createLabeledIconBox(descriptionIcon, "Description: " + reservation.getDescription());
        HBox startLocationBox = createLabeledIconBox(startIcon, "Start: " + reservation.getStartLocation().getAddress());
        HBox endLocationBox = createLabeledIconBox(endIcon, "End: " + reservation.getEndLocation().getAddress());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        // Add all elements to the modal layout
        modalLayout.getChildren().addAll(title, dateBox, statusBox, descriptionBox, startLocationBox, endLocationBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    // Helper method to create an HBox with an icon and a label
    private HBox createLabeledIconBox(ImageView icon, String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox box = new HBox(10); // Spacing between icon and label
        box.getChildren().addAll(icon, label);
        box.setAlignment(Pos.CENTER_LEFT); // Align to the left
        return box;
    }

    // Helper method to create icons
    private ImageView createIcon(String iconPath) {
        Image image = new Image(getClass().getResourceAsStream(iconPath));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20); // Set a consistent height
        imageView.setFitWidth(20); // Set a consistent width
        return imageView;
    }


    private void editReservation(Reservation reservation) {
        // Create Custom Dialog
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Edit Reservation");
        dialog.setHeaderText(null);
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Title Label
        Label titleLabel = new Label("Modify Reservation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(reservation.getDate().toLocalDateTime().toLocalDate());
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("CONFIRMED", "CANCELLED", "COMPLETED", "ON_GOING"); // Update to match enum names
        statusComboBox.setValue(reservation.getStatus().name()); // Set current status as String
        TextField descriptionField = new TextField(reservation.getDescription());
        TextField startLocationField = new TextField(reservation.getStartLocation().getAddress());
        TextField endLocationField = new TextField(reservation.getEndLocation().getAddress());

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Start Location:"), 0, 3);
        grid.add(startLocationField, 1, 3);
        grid.add(new Label("End Location:"), 0, 4);
        grid.add(endLocationField, 1, 4);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, grid);
        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Result conversion to Reservation object
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                if (datePicker.getValue() == null || descriptionField.getText().trim().isEmpty() ||
                        startLocationField.getText().trim().isEmpty() || endLocationField.getText().trim().isEmpty()) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Update reservation fields
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                reservation.setStatus(Reservation.Status.valueOf(statusComboBox.getValue())); // Convert String to Status enum
                reservation.setDescription(descriptionField.getText());
                reservation.getStartLocation().setAddress(startLocationField.getText());
                reservation.getEndLocation().setAddress(endLocationField.getText());
                return reservation;
            }
            return null;
        });

        // Show Dialog & Handle Response
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(updatedReservation -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Update");
            confirmAlert.setHeaderText("Are you sure you want to update this reservation?");
            confirmAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    reservationService.update(updatedReservation);
                    refreshReservations();
                } catch (SQLException e) {
                    showErrorDialog("Error", "An error occurred while updating the reservation: " + e.getMessage());
                }
            }
        });
    }

    private void deleteReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Reservation");
        alert.setHeaderText("Are you sure you want to delete this reservation?");
        alert.setContentText("This action cannot be undone.");

        // Load CSS for better design
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.delete(reservation.getIdReservation());
                refreshReservations();
            } catch (SQLException e) {
                showErrorDialog("Error", "An error occurred while deleting the reservation: " + e.getMessage());
            }
        }
    }

    /**
     * Show an error dialog with a custom message.
     */
    private void showErrorDialog(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }

    private void refreshReservations() {
        stationFlowPane.getChildren().clear();
        loadStationsIntoFlowPane();
    }
}