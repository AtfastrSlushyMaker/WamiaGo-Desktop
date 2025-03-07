package controllers.Reservation;

import entities.Reservation;
import entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.ReservationService;
import services.StationService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ReservationclientController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button,btn_workbench12;
    @FXML
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;

    @FXML
    private Button btn_workbench1000, btn_workbench1001, btn_workbench1002, btn_workbench1003;

    private final ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
        setupNavigation();

        // Charger les réservations du client connecté
        loadStationsIntoFlowPane();
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        //btn_workbench12.setOnAction(event -> loadScene("/Annoucement/Front/announcements_client.fxml"));
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

    @FXML
    public void handleHomeButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the dashboard view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRidesButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rides/rides.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) rides_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the rides view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBookingsButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements_client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) bookings_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the bookings view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogoutButtonAction(ActionEvent event) {
        try {
            // Nettoyer la session
            SessionManager.getInstance().logout();

            // Naviguer vers la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/loginSignup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logout_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadStationsIntoFlowPane() {
        try {
            // Récupérer l'utilisateur connecté
            User loggedInUser = SessionManager.getInstance().getUser();

            // Récupérer les réservations du client connecté
            List<Reservation> reservations = reservationService.getReservationsByClientId(loggedInUser.getId());

            // Ajouter chaque réservation à l'interface utilisateur
            for (Reservation station : reservations) {
                VBox stationCard = createStationCard(station);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Reservation reservation) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(15));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);
        stationCard.setSpacing(8);

        // Image and title
        HBox imageAndTextBox = createImageAndTextBox(reservation);

        // Labels for details
        Label dateLabel = new Label("Date: " + reservation.getDate());
        Label statusLabel = new Label("Status: " + reservation.getStatus());
        Label descriptionLabel = new Label("Description: " + reservation.getDescription());
        Label startLocationLabel = new Label("Start: " + reservation.getStartLocation().getAddress());
        Label endLocationLabel = new Label("End: " + reservation.getEndLocation().getAddress());

        // Appliquer la couleur en fonction du statut
        switch (reservation.getStatus()) {
            case ON_GOING:
                statusLabel.getStyleClass().add("status-label ON_GOING");
                break;
            case CONFIRMED:
                statusLabel.getStyleClass().add("status-label CONFIRMED");
                break;
            case CANCELLED:
                statusLabel.getStyleClass().add("status-label CANCELLED");
                break;
            case COMPLETED:
                statusLabel.getStyleClass().add("status-label COMPLETED");
                break;
        }

        // Buttons with icons
        Button selectButton = createIconButton("/images/icons/eye.png", event -> openStationDetails(reservation));
        Button editButton = createIconButton("/images/icons/edit.png", event -> editReservation(reservation));
        Button deleteButton = createIconButton("/images/icons/delete.png", event -> deleteReservation(reservation));

        // Désactiver le bouton de modification si le statut n'est pas ON_GOING
        if (!reservation.getStatus().equals(Reservation.Status.ON_GOING)) {
            editButton.setDisable(true);
        }

        // Button container
        HBox buttonBox = new HBox(10, selectButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // Add elements to card
        stationCard.getChildren().addAll(imageAndTextBox, dateLabel, statusLabel, descriptionLabel, startLocationLabel, endLocationLabel, buttonBox);

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

    private Button createIconButton(String imagePath, EventHandler<ActionEvent> eventHandler) {
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(20); // Adjust icon size
        icon.setFitHeight(20);

        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
        button.setOnAction(eventHandler);
        return button;
    }

    private HBox createImageAndTextBox(Reservation reservation) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/date.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(reservation.getAnnouncement().getTitle());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2e2e2e;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private void openStationDetails(Reservation station) {
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

        Label titleLabel = new Label(station.getAnnouncement().getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Load icons
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(station.getStatus().equals("Completed") ?
                "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView descriptionIcon = createIcon("/images/icons/description.png");
        ImageView startIcon = createIcon("/images/icons/place.png");
        ImageView endIcon = createIcon("/images/icons/place.png");

        // Labels with icons
        HBox localDateBox = createLabeledIconBox(dateIcon, "Date: " + station.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + station.getStatus());
        HBox descriptionBox = createLabeledIconBox(descriptionIcon, "Description: " + station.getDescription());
        HBox startLocationBox = createLabeledIconBox(startIcon, "Start: " + station.getStartLocation().getAddress());
        HBox endLocationBox = createLabeledIconBox(endIcon, "End: " + station.getEndLocation().getAddress());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(titleLabel, localDateBox, statusBox, descriptionBox, startLocationBox, endLocationBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    // Helper method to create labeled icon boxes
    private HBox createLabeledIconBox(ImageView icon, String label) {
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");

        HBox box = new HBox(10, icon, textLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // Helper method to load icons
    private ImageView createIcon(String path) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        icon.setFitHeight(16);
        icon.setFitWidth(16);
        return icon;
    }

    private void editReservation(Reservation reservation) {
        // Create Custom Dialog
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Edit Reservation");
        dialog.setHeaderText(null); // Cleaner UI without default header
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Title Label
        Label titleLabel = new Label("Modify Reservation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(reservation.getDate().toLocalDateTime().toLocalDate());
//        //ComboBox<String> statusComboBox = new ComboBox<>();
//        statusComboBox.getItems().addAll("Pending", "Confirmed", "Cancelled");
//        statusComboBox.setValue(reservation.getStatus().toString());
        TextField descriptionField = new TextField(reservation.getDescription());
        TextField startLocationField = new TextField(reservation.getStartLocation().getAddress());
        TextField endLocationField = new TextField(reservation.getEndLocation().getAddress());

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
//        grid.add(new Label("Status:"), 0, 1);
//        grid.add(statusComboBox, 1, 1);
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
                if (datePicker.getValue() == null || descriptionField.getText().trim().isEmpty()) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Update reservation fields
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                //reservation.setStatus(Reservation.Status.valueOf(statusComboBox.getValue())); // Update status
                reservation.setDescription(descriptionField.getText());
                // Address fields remain unchanged for complex objects
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
                    loadStationsIntoFlowPane(); // Rafraîchir les réservations après la mise à jour
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


    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements_client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the announcements view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

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