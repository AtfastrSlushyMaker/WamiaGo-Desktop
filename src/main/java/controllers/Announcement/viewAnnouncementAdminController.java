package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.Location;
import entities.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class viewAnnouncementAdminController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private AnchorPane root;
    @FXML
    private FlowPane announcementFlowPane;

    private final AnnouncementService announcementService = new AnnouncementService();
    private final LocationService locationService = new LocationService();
    private final ReservationService reservationService = new ReservationService();

    public viewAnnouncementAdminController(AnchorPane root) {
        this.root = root;
    }




    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("Annoucement/Front/announcementsStyle.css").toExternalForm());
        loadAnnouncementsIntoFlowPane();

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

    private void loadAnnouncementsIntoFlowPane() {
        try {
            List<Announcement> announcements = announcementService.read();
            for (Announcement announcement : announcements) {
                VBox announcementCard = createAnnouncementCard(announcement);
                announcementFlowPane.getChildren().add(announcementCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createAnnouncementCard(Announcement announcement) {
        VBox announcementCard = new VBox(10);
        announcementCard.setPadding(new Insets(10));
        announcementCard.getStyleClass().add("announcement-card-admin");
        announcementCard.setAlignment(Pos.CENTER);

        HBox imageAndTextBox = createImageAndTextBox(announcement);

        Label titleLabel = new Label("Title: " + announcement.getTitle());
        Label contentLabel = new Label("Content: " + announcement.getContent());
        Label dateLabel = new Label("Date: " + announcement.getDate());
        Label zoneLabel = new Label("Zone: " + announcement.getZone());

        Button selectButton = createSelectButton(announcement);
        Button reserveButton = createReserveButton(announcement);

        HBox buttonBox = new HBox(10, selectButton, reserveButton);
        buttonBox.setAlignment(Pos.CENTER);

        announcementCard.getChildren().addAll(imageAndTextBox, titleLabel, contentLabel, dateLabel, zoneLabel, buttonBox);

        announcementCard.setOnMouseEntered(event -> {
            announcementCard.setScaleX(1.05);
            announcementCard.setScaleY(1.05);
        });

        announcementCard.setOnMouseExited(event -> {
            announcementCard.setScaleX(1);
            announcementCard.setScaleY(1);
        });

        return announcementCard;
    }

    private HBox createImageAndTextBox(Announcement announcement) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView announcementImage = new ImageView(new Image(getClass().getResource("/images/icons/announcement_icon.png").toExternalForm()));
        announcementImage.setFitWidth(50);
        announcementImage.setFitHeight(50);

        Text nameText = new Text(announcement.getTitle());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(announcementImage, nameText);
        return hbox;
    }

    private Button createSelectButton(Announcement announcement) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("announcement-button");
        selectButton.setOnAction(e -> openAnnouncementDetails(announcement));
        return selectButton;
    }

    private Button createReserveButton(Announcement announcement) {
        Button reserveButton = new Button("Reserve");
        reserveButton.getStyleClass().add("announcement-button");
        reserveButton.setOnAction(e -> handleReserve(announcement));
        return reserveButton;
    }

    private void openAnnouncementDetails(Announcement announcement) {
        Stage modalStage = new Stage();
        modalStage.setTitle(announcement.getTitle());
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Title: " + announcement.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Load icons for details
        ImageView contentIcon = createIcon("/images/icons/content.png");
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView zoneIcon = createIcon("/images/icons/zone.png");
        ImageView statusIcon = createIcon(announcement.getStatus() ? "/images/icons/check.png" : "/images/icons/cross.png");

        // Create HBoxes for labels with icons
        HBox contentBox = createLabeledIconBox(contentIcon, "Content: " + announcement.getContent());
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + announcement.getDate());
        HBox zoneBox = createLabeledIconBox(zoneIcon, "Zone: " + announcement.getZone());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + (announcement.getStatus() ? "Active" : "Inactive"));

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
        modalLayout.getChildren().addAll(titleLabel, contentBox, dateBox, zoneBox, statusBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    // Helper method to create an HBox with an icon and a label
    private HBox createLabeledIconBox(ImageView icon, String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

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

    private void handleReserve(Announcement announcement) {
        // Create Custom Dialog
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Reserve Announcement");
        dialog.setHeaderText(null); // Cleaner UI without default header
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Title Label
        Label titleLabel = new Label("Enter Reservation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descriptionField = new TextField();
        ComboBox<String> startLocationComboBox = new ComboBox<>();
        ComboBox<String> endLocationComboBox = new ComboBox<>();

        // Load Locations
        try {
            List<Location> locations = locationService.read();
            for (Location location : locations) {
                startLocationComboBox.getItems().add(location.getAddress());
                endLocationComboBox.getItems().add(location.getAddress());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error", "Failed to load locations: " + e.getMessage());
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Start Location:"), 0, 2);
        grid.add(startLocationComboBox, 1, 2);
        grid.add(new Label("End Location:"), 0, 3);
        grid.add(endLocationComboBox, 1, 3);

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
                // Validate inputs
                if (datePicker.getValue() == null || descriptionField.getText().isEmpty() ||
                        startLocationComboBox.getValue() == null || endLocationComboBox.getValue() == null) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Create the reservation
                Reservation reservation = new Reservation();
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                reservation.setDescription(descriptionField.getText());

                try {
                    // Get Location objects from addresses
                    Location startLocation = locationService.getByAddress(startLocationComboBox.getValue());
                    Location endLocation = locationService.getByAddress(endLocationComboBox.getValue());
                    reservation.setStartLocation(startLocation);
                    reservation.setEndLocation(endLocation);

                    // Associate the announcement with the reservation
                    reservation.setAnnouncement(announcement);

                    // Optionally link to the transporter if needed
                    // Driver transporter = new DriverService().getById(announcement.getTransporter().getIdDriver());
                    // reservation.setTransporter(transporter); // Uncomment if needed
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorDialog("Error", "Failed to create reservation: " + e.getMessage());
                    return null;
                }

                return reservation;
            }
            return null;
        });

        // Show Dialog & Handle Response
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            try {
                // Save the reservation to the database
                reservationService.create(reservation);
                refreshAnnouncements();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Error", "An error occurred while creating the reservation: " + e.getMessage());
            }
        });
    }

    // Helper method to show error dialogs
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshAnnouncements() {
        announcementFlowPane.getChildren().clear();
        loadAnnouncementsIntoFlowPane();
    }
}
