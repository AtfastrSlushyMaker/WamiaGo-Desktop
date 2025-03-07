package controllers.Relocation;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.RelocationService;
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

public class RelocationController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private AnchorPane root;
    @FXML
    private FlowPane stationFlowPane;

    private final RelocationService relocationService = new RelocationService();


    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Relocation/front/relocation.css").toExternalForm());
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
            for (Relocation station : relocationService.read()) {
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
        stationCard.getStyleClass().add("station-card-admin");
        stationCard.setAlignment(Pos.CENTER);

        ImageView topImage = new ImageView(new Image(getClass().getResource("/images/icons/demenagement.png").toExternalForm()));
        topImage.setFitWidth(100);
        topImage.setFitHeight(100);
        topImage.setPreserveRatio(true);

        Text titleText = new Text(station.getReservation().getDescription());
        titleText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #333333;");

        Label reservationLabel = new Label("Reservation: " + station.getReservation().getDescription());
        Label dateLabel = new Label("Date: " + station.getDate());
        Label costLabel = new Label("Cost: " + station.getCost());

        Button selectButton = createSelectButton(station);
        //Button editButton = createIconButton("/images/icons/edit.png", () -> editRelocation(station));
        Button deleteButton = createIconButton("/images/icons/delete.png", () -> deleteRelocation(station));

        HBox buttonBox = new HBox(10, selectButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        stationCard.getChildren().addAll(topImage, titleText, reservationLabel, dateLabel, costLabel, buttonBox);

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

    private Button createIconButton(String iconPath, Runnable action) {
        ImageView iconView = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        iconView.setFitWidth(20);
        iconView.setFitHeight(20);

        Button button = new Button();
        button.setGraphic(iconView);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
        button.setOnAction(e -> action.run());

        return button;
    }

    private Button createSelectButton(Relocation station) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("station-button-admin");
        selectButton.setOnAction(e -> openStationDetails(station));
        return selectButton;
    }


    private void openStationDetails(Relocation station) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Station Details");
        modalStage.initModality(Modality.APPLICATION_MODAL); // Make the modal stage application-modal

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // Slightly transparent for focus

        VBox modalLayout = new VBox(15); // Increased spacing for a more airy layout
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; " + // Changed to white for better contrast
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);"); // Added drop shadow for depth

        // Title label with larger font
        Label title = new Label(station.getReservation().getDescription());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Load icons for each detail
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(station.isStatus() ? "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView costIcon = createIcon("/images/icons/money.png");

        // Create labeled icon boxes
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + station.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + (station.isStatus() ? "Completed" : "Pending"));
        HBox costBox = createLabeledIconBox(costIcon, "Cost: " + station.getCost());

        // Close button styling
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #007BFF; " + // Calming blue for the button
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        // Add all elements to the modal layout
        modalLayout.getChildren().addAll(title, dateBox, statusBox, costBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300); // Keep the size consistent
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
        // Create Custom Dialog
        Dialog<Relocation> dialog = new Dialog<>();
        dialog.setTitle("Edit Relocation");
        dialog.setHeaderText(null); // Cleaner UI without default header
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Title Label
        Label titleLabel = new Label("Modify Relocation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Create Input Fields
        DatePicker datePicker = new DatePicker(relocation.getDate().toLocalDateTime().toLocalDate());
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Pending", "Completed");
        statusComboBox.setValue(relocation.isStatus() ? "Completed" : "Pending");
        TextField costField = new TextField(String.valueOf(relocation.getCost()));

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);
        grid.add(new Label("Cost:"), 0, 2);
        grid.add(costField, 1, 2);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, grid);
        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Result conversion to Relocation object
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                if (datePicker.getValue() == null || costField.getText().trim().isEmpty()) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Update relocation fields
                relocation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                relocation.setCost(Float.parseFloat(costField.getText())); // Update cost
                relocation.setStatus(statusComboBox.getValue().equals("Completed")); // Update status
                return relocation;
            }
            return null;
        });

        // Show Dialog & Handle Response
        Optional<Relocation> result = dialog.showAndWait();
        result.ifPresent(updatedRelocation -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Update");
            confirmAlert.setHeaderText("Are you sure you want to update this relocation?");
            confirmAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    relocationService.update(updatedRelocation);
                    refreshRelocations();
                } catch (SQLException e) {
                    showErrorDialog("Error", "An error occurred while updating the relocation: " + e.getMessage());
                }
            }
        });
    }

    private void deleteRelocation(Relocation relocation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Relocation");
        alert.setHeaderText("Are you sure you want to delete this relocation?");
        alert.setContentText("This action cannot be undone.");

        // Load CSS for better design
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                relocationService.delete(relocation.getIdRelocation());
                refreshRelocations();
            } catch (SQLException e) {
                showErrorDialog("Error", "An error occurred while deleting the relocation: " + e.getMessage());
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