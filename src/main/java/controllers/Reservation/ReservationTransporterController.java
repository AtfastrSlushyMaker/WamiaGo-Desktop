package controllers.Reservation;

import entities.Driver;
import entities.Reservation;
import entities.Relocation;
import entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.DriverService;
import services.RelocationService;
import services.ReservationService;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationTransporterController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private HBox root;
    @FXML
    private GridPane stationGridPane;

    private Driver currentDriver;
    private User loggedInUser = SessionManager.getInstance().getUser();

    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
        setupNavigation();

        // Récupérer le conducteur actuel en fonction de l'utilisateur connecté
        DriverService driverService = new DriverService();
        try {
            currentDriver = driverService.getById(loggedInUser.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Charger les réservations du conducteur connecté
        loadStationsIntoGridPane();
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

    private void loadStationsIntoGridPane() {
        stationGridPane.getChildren().clear();

        int column = 0;
        int row = 0;

        try {
            // Récupérer les réservations du conducteur connecté
            List<Reservation> reservations = reservationService.getReservationsByDriverId(currentDriver.getIdDriver());

            // Ajouter chaque réservation à l'interface utilisateur
            for (Reservation reservation : reservations) {
                VBox stationCard = createStationCard(reservation);
                stationGridPane.add(stationCard, column, row);

                column++;
                if (column == 3) { // Adjust to 3 cards per row
                    column = 0;
                    row++;
                }
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
        Label clientLabel = new Label("Client: " + reservation.getUser().getEmail());

        // Buttons with icons
        Button selectButton = createIconButton("/images/icons/eye.png", event -> openStationDetails(reservation));
        Button acceptButton = createIconButton("/images/icons/check.png", event -> handleAccept(reservation));
        Button refuseButton = createIconButton("/images/icons/refuse.png", event -> handleRefuse(reservation));

        // Button container: Select next to Edit, Delete aligned
        HBox buttonBox = new HBox(10, selectButton, acceptButton, refuseButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // Add elements to card
        stationCard.getChildren().addAll(imageAndTextBox, dateLabel, statusLabel, descriptionLabel, startLocationLabel, endLocationLabel, clientLabel, buttonBox);

        // Appliquer les styles en fonction du statut
        switch (reservation.getStatus()) {
            case CANCELLED:
                stationCard.setStyle("-fx-opacity: 0.5; -fx-background-color: #f0f0f0;");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                acceptButton.setDisable(true);
                refuseButton.setDisable(true);
                break;
            case CONFIRMED:
                stationCard.setStyle("-fx-opacity: 0.5; -fx-background-color: #f0f0f0;");
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                acceptButton.setDisable(true);
                refuseButton.setDisable(true);
                break;
            case COMPLETED:
                stationCard.setStyle("-fx-opacity: 0.5; -fx-background-color: #f0f0f0;");
                statusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                acceptButton.setDisable(true);
                refuseButton.setDisable(true);
                break;
            default:
                // Pas de style particulier pour les autres statuts
                break;
        }

        // Animation au survol
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
        ImageView clientIcon = createIcon("/images/icons/client.png");

        // Labels with icons
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + reservation.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + reservation.getStatus());
        HBox descriptionBox = createLabeledIconBox(descriptionIcon, "Description: " + reservation.getDescription());
        HBox startLocationBox = createLabeledIconBox(startIcon, "Start: " + reservation.getStartLocation().getAddress());
        HBox endLocationBox = createLabeledIconBox(endIcon, "End: " + reservation.getEndLocation().getAddress());
        HBox clientBox = createLabeledIconBox(clientIcon, "Client: " + reservation.getUser().getEmail());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(title, dateBox, statusBox, descriptionBox, startLocationBox, endLocationBox, clientBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }


    // Helper method to create an icon
    private ImageView createIcon(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        return imageView;
    }

    // Helper method to create a label with an icon
    private HBox createLabeledIconBox(ImageView icon, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-text-fill: #333;");
        HBox hBox = new HBox(10, icon, label);
        hBox.setAlignment(Pos.CENTER_LEFT);
        return hBox;
    }

    private void handleAccept(Reservation reservation) {
        // Create Custom Dialog
        Dialog<Relocation> dialog = new Dialog<>();
        dialog.setTitle("Accept Reservation");
        dialog.setHeaderText(null); // Cleaner UI without default header
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Icon
        ImageView acceptIcon = new ImageView(new Image(getClass().getResource("/images/icons/check.png").toExternalForm()));
        acceptIcon.setFitWidth(50);
        acceptIcon.setFitHeight(50);

        // Title Label
        Label titleLabel = new Label("Enter Relocation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #303030;");

        // Form Fields
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField costField = new TextField();
        costField.setPromptText("Enter cost...");

        // Grid Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Cost:"), 0, 1);
        grid.add(costField, 1, 1);

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(acceptIcon, titleLabel, grid);
        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Input Validation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                if (datePicker.getValue() == null || costField.getText().trim().isEmpty()) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Create a new Relocation object
                Relocation relocation = new Relocation();
                relocation.setReservation(reservation);
                relocation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                relocation.setStatus(true); // Assuming status is true for accepted relocations
                relocation.setCost(Float.parseFloat(costField.getText()));

                return relocation;
            }
            return null;
        });

        // Show Dialog & Handle Result
        Optional<Relocation> result = dialog.showAndWait();
        result.ifPresent(relocation -> {
            try {
                // Update the reservation status to CONFIRMED
                reservation.setStatus(Reservation.Status.CONFIRMED);
                reservationService.update(reservation);

                // Save the relocation
                RelocationService relocationService = new RelocationService();
                relocationService.create(relocation);

                // Refresh the reservations list
                refreshReservations();
            } catch (SQLException e) {
                showErrorDialog("Error Updating Reservation", e.getMessage());
            }
        });
    }

    private void handleRefuse(Reservation reservation) {
        // Create Custom Dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Refuse Reservation");
        dialog.setHeaderText(null);

        // Load CSS
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Icon
        ImageView refuseIcon = new ImageView(new Image(getClass().getResource("/images/icons/refuse.png").toExternalForm()));
        refuseIcon.setFitWidth(50);
        refuseIcon.setFitHeight(50);

        // Title Label
        Label titleLabel = new Label("Are you sure you want to refuse this reservation?");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");

        // Message Label
        Label messageLabel = new Label("This action cannot be undone.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(refuseIcon, titleLabel, messageLabel);
        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType refuseButton = new ButtonType("Refuse", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(refuseButton, cancelButton);

        // Show Dialog & Handle Result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == refuseButton) {
            try {
                reservation.setStatus(Reservation.Status.CANCELLED);
                reservationService.update(reservation);
                refreshReservations();
            } catch (SQLException e) {
                showErrorDialog("Error Refusing Reservation", e.getMessage());
            }
        }
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements.fxml"));
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
        stationGridPane.getChildren().clear();
        loadStationsIntoGridPane();
    }
}