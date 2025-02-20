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
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;

    private final RelocationService relocationService = new RelocationService();


    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Relocation/front/relocation.css").toExternalForm());
        loadStationsIntoFlowPane();
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
        stationCard.setPadding(new Insets(10));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);

        HBox imageAndTextBox = createImageAndTextBox(station);

        Label Reservation = new Label("Reservation: " + station.getReservation().getDescription());
        Label Date = new Label("Date: " + station.getDate());
        Label Cost = new Label("Cost: " + station.getCost());
        //Label transporteur = new Label("Transporter: " + new UserService().getById(station.getAnnouncement().getTransporter().getIdDriver());

        Button selectButton = createSelectButton(station);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> editRelocation(station));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteRelocation(station));

        HBox buttonBox = new HBox(10, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        stationCard.getChildren().addAll(imageAndTextBox, Reservation, Date, Cost,  selectButton, buttonBox);

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

    private HBox createImageAndTextBox(Relocation station) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/public-transport_3061677.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(station.getReservation().getDescription());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private Button createSelectButton(Relocation station) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("station-button");
        selectButton.setOnAction(e -> openStationDetails(station));
        return selectButton;
    }

    private void openStationDetails(Relocation station) {
        Stage modalStage = new Stage();
        modalStage.setTitle(station.getReservation().getDescription());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label localDate = new Label("Date: " + station.getDate());
        localDate.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        String statusText = station.isStatus() ? "Completed" : "Pending";
        Label statusLabel = new Label("Status: " + statusText);
        statusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");


        Label cost = new Label("Cost: " + station.getCost());
        cost.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");



        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("station-bike-close-button");

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll( localDate, statusLabel,cost, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void editRelocation(Relocation relocation) {
        // Création du Dialog personnalisé
        Dialog<Relocation> dialog = new Dialog<>();
        dialog.setTitle("Edit Relocation");
        dialog.setHeaderText("Modify relocation details");

        // Création des champs de saisie
        DatePicker datePicker = new DatePicker(relocation.getDate().toLocalDateTime().toLocalDate()); // Convertir Timestamp en LocalDate
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Pending", "Completed"); // Ajout des statuts possibles
        statusComboBox.setValue(relocation.isStatus() ? "Completed" : "Pending"); // Définir la valeur actuelle
        TextField costField = new TextField(String.valueOf(relocation.getCost())); // Champ pour le coût

        // Ajout des champs au Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusComboBox, 1, 1);
        grid.add(new Label("Cost:"), 0, 2);
        grid.add(costField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Ajout des boutons OK et Annuler
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Conversion des résultats en objet Relocation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Mettre à jour les champs de la relocation
                relocation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay())); // Convertir LocalDate en Timestamp
               // relocation.setFloat(String.valueOf(costField));
                return relocation;
            }
            return null;
        });

        // Affichage du Dialog et gestion de la réponse
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
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred while updating the relocation.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void deleteRelocation(Relocation relocation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Relocation");
        alert.setHeaderText("Are you sure you want to delete this relocation?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                relocationService.delete(relocation.getIdRelocation());
                refreshRelocations();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("An error occurred while deleting the relocation.");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    private void refreshRelocations() {
        stationFlowPane.getChildren().clear();
        loadStationsIntoFlowPane();
    }
}
