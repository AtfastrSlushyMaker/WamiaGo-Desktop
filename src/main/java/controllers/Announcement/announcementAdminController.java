package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
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
import services.AnnouncementService;
import javafx.geometry.Pos;
import javafx.scene.image.Image;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;


public class announcementAdminController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private AnchorPane root;
    @FXML
    private FlowPane announcementFlowPane;

    private final AnnouncementService announcementService = new AnnouncementService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Annoucement/Front/announcementAdmin.css").toExternalForm());
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
            for (Announcement announcement : announcementService.read()) {
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
        announcementCard.setAlignment(Pos.CENTER);

        announcementCard.setStyle("-fx-background-color: #f0f8ff; " +
                "-fx-background-radius: 45px; " +
                "-fx-border-radius: 45px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5); " +
                "-fx-padding: 10px; " +
                "-fx-pref-width: 350px; " +
                "-fx-cursor: hand; " +
                "-fx-transition: all 0.3s;");

        HBox imageAndTextBox = createImageAndTextBox(announcement);

        Label title = new Label("Title: " + announcement.getTitle());
        Label content = new Label("Content: " + announcement.getContent());
        Label date = new Label("Date: " + announcement.getDate());
        Label zone = new Label("Zone: " + announcement.getZone());
        Label status = new Label("Status: " + (announcement.getStatus() ? "Active" : "Inactive"));

        // Create the buttons
        Button selectButton = new Button("Select");
        selectButton.setOnAction(e -> openAnnouncementDetails(announcement));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteAnnouncement(announcement));

        selectButton.getStyleClass().add("announcement-button-admin");
        deleteButton.getStyleClass().add("announcement-button-admin");

        // Create a single HBox for the buttons
        HBox buttonBox = new HBox(10, selectButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add components to the announcement card
        announcementCard.getChildren().addAll(imageAndTextBox, title, content, date, zone, status, buttonBox);

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
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER); // Center the HBox

        // Create a VBox for the icon and title
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER); // Center the VBox
        vbox.setSpacing(5); // Add some spacing between icon and title

        ImageView announcementImage = new ImageView(new Image(getClass().getResource("/images/icons/announcement.png").toExternalForm()));
        announcementImage.setFitWidth(50);
        announcementImage.setFitHeight(50);

        // Use TextFlow to center the text
        TextFlow titleTextFlow = new TextFlow();
        Text titleText = new Text(announcement.getTitle());
        titleText.setWrappingWidth(180);
        titleText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #3a3a3a;");

        titleTextFlow.getChildren().add(titleText);
        titleTextFlow.setTextAlignment(TextAlignment.CENTER); // Center the text in the TextFlow

        // Add the ImageView and TextFlow to the VBox
        vbox.getChildren().addAll(announcementImage, titleTextFlow);

        // Add the VBox to the HBox
        hbox.getChildren().add(vbox);

        return hbox;
    }

    private void openAnnouncementDetails(Announcement announcement) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Details");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);");

        Label title = new Label(announcement.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Load icons
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(announcement.getStatus() ? "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView contentIcon = createIcon("/images/icons/description.png");
        ImageView zoneIcon = createIcon("/images/icons/place.png");

        // Create HBox for labels with icons
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + announcement.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + (announcement.getStatus() ? "Active" : "Inactive"));
        HBox contentBox = createLabeledIconBox(contentIcon, "Content: " + announcement.getContent());
        HBox zoneBox = createLabeledIconBox(zoneIcon, "Zone: " + announcement.getZone());

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
        modalLayout.getChildren().addAll(title, dateBox, statusBox, contentBox, zoneBox, closeButtonContainer);
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

    private void deleteAnnouncement(Announcement announcement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Announcement");
        alert.setHeaderText("Are you sure you want to delete this announcement?");
        alert.setContentText("This action cannot be undone.");

        // Load CSS for better design
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                announcementService.delete(announcement.getIdAnnouncement());
                refreshAnnouncements();
            } catch (SQLException e) {
                showErrorDialog("Error", "An error occurred while deleting the announcement: " + e.getMessage());
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

    private void refreshAnnouncements() {
        announcementFlowPane.getChildren().clear();
        loadAnnouncementsIntoFlowPane();
    }
}
