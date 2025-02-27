package controllers.Relocation;

import entities.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RelocationControllerTransporter {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;
    private Driver currentDriver;
    private User loggedInUser = SessionManager.getInstance().getUser(); // Utilisateur connecté

    private final RelocationService relocationService = new RelocationService();


    @FXML
    public void initialize() throws SQLException {
        root.getStylesheets().add(getClass().getResource("/Relocation/front/relocation.css").toExternalForm());
        setupNavigation();

        // Récupérer le conducteur actuel en fonction de l'utilisateur connecté
        DriverService driverService = new DriverService();
        currentDriver = driverService.getById(loggedInUser.getId());

        // Charger les relocalisations du conducteur connecté
        loadStationsIntoFlowPane();
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

    private void loadStationsIntoFlowPane() {
        try {
            // Récupérer les relocalisations du conducteur connecté
            List<Relocation> relocations = relocationService.getRelocationsByDriverId(currentDriver.getIdDriver());

            // Ajouter chaque relocalisation à l'interface utilisateur
            for (Relocation station : relocations) {
                VBox stationCard = createStationCard(station);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Relocation station) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(15));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);
        stationCard.setSpacing(8);

        // Image and title
        HBox imageAndTextBox = createImageAndTextBox(station);

        // Labels for details
        Label reservationLabel = new Label("Reservation: " + station.getReservation().getDescription());
        Label dateLabel = new Label("Date: " + station.getDate());
        Label costLabel = new Label("Cost: " + station.getCost());

        // Buttons with icons
        Button selectButton = createIconButton("/images/icons/eye.png", event -> openStationDetails(station));
        Button editButton = createIconButton("/images/icons/edit.png", event -> editRelocation(station));
        Button deleteButton = createIconButton("/images/icons/delete.png", event -> deleteRelocation(station));

        // Button container: Select next to Edit, Delete aligned
        HBox buttonBox = new HBox(10, selectButton, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // Add elements to card
        stationCard.getChildren().addAll(imageAndTextBox, reservationLabel, dateLabel, costLabel, buttonBox);

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

    private HBox createImageAndTextBox(Relocation station) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/relocation.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(station.getReservation().getDescription());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2e2e2e;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private void openStationDetails(Relocation station) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Détails");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; " + // Changed to white
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);");

        Label title = new Label(station.getReservation().getDescription());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;"); // Darker text for contrast

        // Load icons
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(station.isStatus() ? "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView costIcon = createIcon("/images/icons/money.png");

        // Labels with icons
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + station.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + (station.isStatus() ? "Completed" : "Pending"));
        HBox costBox = createLabeledIconBox(costIcon, "Cost: " + station.getCost());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " + // Changed button color to a calming blue
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " + // Reduced radius
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(title, dateBox, statusBox, costBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 280); // Reduced size
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    /**
     * Creates an ImageView with a specific icon size.
     */
    private ImageView createIcon(String imagePath) {
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        return icon;
    }

    /**
     * Creates an HBox containing an icon and a label for a consistent layout.
     */
    private HBox createLabeledIconBox(ImageView icon, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;"); // Darker text for contrast

        HBox box = new HBox(8, icon, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void editRelocation(Relocation relocation) {
        // Create a Custom Dialog
        Dialog<Relocation> dialog = new Dialog<>();
        dialog.setTitle("Edit Relocation");
        dialog.setHeaderText(null); // Remove default header for a cleaner look

        // Set Dialog Style
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Icon
        ImageView editIcon = new ImageView(new Image(getClass().getResource("/images/icons/edit.png").toExternalForm()));
        editIcon.setFitWidth(50);
        editIcon.setFitHeight(50);

        // Title Label
        Label titleLabel = new Label("Modify Relocation Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // Date Picker
        DatePicker datePicker = new DatePicker(relocation.getDate().toLocalDateTime().toLocalDate());

        // Cost Field
        TextField costField = new TextField(String.valueOf(relocation.getCost()));

        // Custom Styling
        costField.setStyle("-fx-font-size: 14px;");
        datePicker.setStyle("-fx-font-size: 14px;");

        // Grid Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 0, 0));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Cost:"), 0, 2);
        grid.add(costField, 1, 2);

        // Layout for Title & Icon
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(editIcon, titleLabel, grid);

        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Handle Result
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Mettre à jour la date et le coût de la relocalisation
                relocation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                relocation.setCost(Float.parseFloat(costField.getText()));
                return relocation;
            }
            return null;
        });

        // Show Dialog
        Optional<Relocation> result = dialog.showAndWait();
        result.ifPresent(updatedRelocation -> {
            showConfirmationDialog(updatedRelocation);
        });
    }
    /**
     * Confirmation Dialog Before Saving Changes
     */
    private void showConfirmationDialog(Relocation updatedRelocation) {
        // Custom Confirmation Dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm Update");
        dialog.setHeaderText(null); // Remove default header for a cleaner look

        // Apply CSS Styles
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Icon
        ImageView confirmIcon = new ImageView(new Image(getClass().getResource("/images/icons/check.png").toExternalForm()));
        confirmIcon.setFitWidth(50);
        confirmIcon.setFitHeight(50);

        // Title Label
        Label titleLabel = new Label("Are you sure you want to update this relocation?");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #292929;");

        // Message Label
        Label messageLabel = new Label("This action cannot be undone.");
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        // Layout
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(confirmIcon, titleLabel, messageLabel);

        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        // Show Dialog & Handle Result
        Optional<ButtonType> confirmResult = dialog.showAndWait();
        if (confirmResult.isPresent() && confirmResult.get() == confirmButton) {
            try {
                // Mettre à jour la relocalisation dans la base de données
                relocationService.update(updatedRelocation);
                // Rafraîchir l'interface utilisateur
                refreshRelocations();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("An error occurred while updating the relocation.", e.getMessage());
            }
        }
    }


    private void deleteRelocation(Relocation relocation) {
        // Create a custom dialog
        Dialog<ButtonType> alert = new Dialog<>();
        alert.setTitle("Delete Relocation");
        alert.setHeaderText(null); // Remove default header for a cleaner look

        // Custom layout
        VBox dialogPaneContent = new VBox(15);
        dialogPaneContent.setPadding(new Insets(20));
        dialogPaneContent.setAlignment(Pos.CENTER);
        dialogPaneContent.setStyle("-fx-background-color: white;");

        // Delete Icon
        ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/images/icons/delete.png").toExternalForm()));
        deleteIcon.setFitWidth(60);
        deleteIcon.setFitHeight(60);

        // Title Label
        Label titleLabel = new Label("Are you sure?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #d9534f;"); // Red color for warning effect

        // Description Label
        Label descriptionLabel = new Label("This action cannot be undone.\nDo you really want to delete this relocation?");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);

        // Add elements to layout
        dialogPaneContent.getChildren().addAll(deleteIcon, titleLabel, descriptionLabel);

        // Set the custom content
        alert.getDialogPane().setContent(dialogPaneContent);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Custom buttons
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().addAll(deleteButton, cancelButton);

        // Show and handle the response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == deleteButton) {
            try {
                relocationService.delete(relocation.getIdRelocation());
                refreshRelocations();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("An error occurred while deleting the relocation.", e.getMessage());
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


    private void refreshRelocations() {
        stationFlowPane.getChildren().clear();
        loadStationsIntoFlowPane();
    }
}