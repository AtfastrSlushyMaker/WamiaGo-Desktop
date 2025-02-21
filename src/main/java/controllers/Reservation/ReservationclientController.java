package controllers.Reservation;

import entities.Reservation;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.ReservationService;
import services.StationService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

public class ReservationclientController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button,btn_workbench12;
    @FXML
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;

    private final ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
        loadStationsIntoFlowPane();
        setupNavigation();
        User user = SessionManager.getInstance().getUser();

    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        btn_workbench12.setOnAction(event -> loadScene("/Annoucement/Front/announcements_client.fxml"));
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
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);

        HBox imageAndTextBox = createImageAndTextBox(station);

        Label localDate = new Label("Date: " + station.getDate());
        Label status = new Label("Status: " + station.getStatus());
        Label description = new Label("Description: " + station.getDescription());
        Label startLocation = new Label("Start: " + station.getStartLocation().getAddress());
        Label endLocation = new Label("End: " + station.getEndLocation().getAddress());

        //Label transporteur = new Label("Transporter: " + new UserService().getById(station.getAnnouncement().getTransporter().getIdDriver());

        Button selectButton = createSelectButton(station);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> editReservation(station));

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteReservation(station));

        HBox buttonBox = new HBox(10, editButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        stationCard.getChildren().addAll(imageAndTextBox, localDate, status, description, startLocation, endLocation, selectButton, buttonBox);

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
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/public-transport_3061677.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(station.getAnnouncement().getTitle());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private Button createSelectButton(Reservation station) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("station-button");
        selectButton.setOnAction(e -> openStationDetails(station));
        return selectButton;
    }

    private void openStationDetails(Reservation station) {
        Stage modalStage = new Stage();
        modalStage.setTitle(station.getAnnouncement().getTitle());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Detail for: " + station.getAnnouncement().getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label localDate = new Label("Date: " + station.getDate());
        localDate.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label status = new Label("Status: " + station.getStatus());
        status.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label description = new Label("Description: " + station.getDescription());
        description.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label startLocation = new Label("Start: " + station.getStartLocation().getAddress());
        startLocation.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label endLocation = new Label("End: " + station.getEndLocation().getAddress());
        endLocation.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("station-bike-close-button");

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll(titleLabel, localDate, status, description, startLocation, endLocation, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void editReservation(Reservation reservation) {
        // Création du Dialog personnalisé

        SessionManager sessionManager = SessionManager.getInstance();
        User user = sessionManager.getUser();
        int loggedInUserId = user.getId();

        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Edit Reservation");
        dialog.setHeaderText("Modify reservation details");

        // Création des champs de saisie
        DatePicker datePicker = new DatePicker(reservation.getDate().toLocalDateTime().toLocalDate()); // Convertir Timestamp en LocalDate
        ComboBox<String> statusComboBox = new ComboBox<>();
//        statusComboBox.getItems().addAll("Pending", "Confirmed", "Cancelled");
//        statusComboBox.setValue(reservation.getStatus());
        TextField descriptionField = new TextField(reservation.getDescription());
        TextField startLocationField = new TextField(reservation.getStartLocation().getAddress());
        TextField endLocationField = new TextField(reservation.getEndLocation().getAddress());

        // Ajout des champs au Dialog
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

        dialog.getDialogPane().setContent(grid);

        // Ajout des boutons OK et Annuler
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Conversion des résultats en objet Reservation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Mettre à jour les champs de la réservation
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay())); // Convertir LocalDate en Timestamp
                //reservation.setStatus(statusComboBox.getValue());
                reservation.setDescription(descriptionField.getText());
                // Ne pas mettre à jour les adresses directement, car ce sont des objets complexes
                return reservation;
            }
            return null;
        });

        // Affichage du Dialog et gestion de la réponse
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
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred while updating the reservation.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void deleteReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Reservation");
        alert.setHeaderText("Are you sure you want to delete this reservation?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.delete(reservation.getIdReservation());
                refreshReservations();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("An error occurred while deleting the reservation.");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    private void refreshReservations() {
        stationFlowPane.getChildren().clear();
        loadStationsIntoFlowPane();
    }
}
