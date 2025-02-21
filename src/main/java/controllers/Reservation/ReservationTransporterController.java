package controllers.Reservation;

import entities.Driver;
import entities.Reservation;
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
import services.ReservationService;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

public class ReservationTransporterController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;

    private Driver currentDriver;
    private User loggedInUser = SessionManager.getInstance().getUser();

    private final ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
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
            for (Reservation reservation : reservationService.read()) {
                VBox stationCard = createStationCard(reservation);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Reservation reservation) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(10));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);

        HBox imageAndTextBox = createImageAndTextBox(reservation);

        Label localDate = new Label("Date: " + reservation.getDate());
        Label status = new Label("Status: " + reservation.getStatus());
        Label description = new Label("Description: " + reservation.getDescription());
        Label startLocation = new Label("Start: " + reservation.getStartLocation().getAddress());
        Label endLocation = new Label("End: " + reservation.getEndLocation().getAddress());

        Button selectButton = createSelectButton(reservation);

        Button acceptButton = new Button("Accept");
        acceptButton.setOnAction(e -> handleAccept(reservation));

        Button refuseButton = new Button("Refuse");
        refuseButton.setOnAction(e -> handleRefuse(reservation));

        HBox buttonBox = new HBox(10, acceptButton, refuseButton);
        buttonBox.setAlignment(Pos.CENTER);

        stationCard.getChildren().addAll(imageAndTextBox, localDate, status, description, startLocation, endLocation, selectButton, buttonBox);

        // Désactiver les boutons si la réservation est déjà traitée
        if (reservation.getStatus() == Reservation.Status.CANCELLED || reservation.getStatus() == Reservation.Status.COMPLETED) {
            stationCard.setStyle("-fx-opacity: 0.5;"); // Griser la carte
            selectButton.setDisable(true);
            acceptButton.setDisable(true);
            refuseButton.setDisable(true);
        }

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

    private HBox createImageAndTextBox(Reservation reservation) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/public-transport_3061677.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(reservation.getAnnouncement().getTitle());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private Button createSelectButton(Reservation reservation) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("station-button");
        selectButton.setOnAction(e -> openStationDetails(reservation));
        return selectButton;
    }

    private void openStationDetails(Reservation reservation) {
        Stage modalStage = new Stage();
        modalStage.setTitle(reservation.getAnnouncement().getTitle());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Detail for: " + reservation.getAnnouncement().getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label localDate = new Label("Date: " + reservation.getDate());
        localDate.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label status = new Label("Status: " + reservation.getStatus());
        status.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label description = new Label("Description: " + reservation.getDescription());
        description.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label startLocation = new Label("Start: " + reservation.getStartLocation().getAddress());
        startLocation.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label endLocation = new Label("End: " + reservation.getEndLocation().getAddress());
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

    private void handleAccept(Reservation reservation) {

        // Création du formulaire pour l'ajout d'une relocalisation
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Accept Reservation");
        dialog.setHeaderText("Enter relocation details");

        // Création des champs de saisie
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField costField = new TextField();

        // Ajout des champs au Dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Cost:"), 0, 1);
        grid.add(costField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Ajout des boutons OK et Annuler
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Conversion des résultats en objet Reservation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Vérifier que les champs sont remplis
                if (datePicker.getValue() == null || costField.getText().isEmpty()) {
                    // Afficher un message d'erreur si les champs ne sont pas remplis
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please fill in all fields.");

                    // Appliquer un style CSS personnalisé pour le message d'erreur
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
                    dialogPane.getStyleClass().add("error-alert");

                    alert.showAndWait();
                    return null; // Ne pas retourner de réservation si les champs ne sont pas remplis
                }

                // Mettre à jour les champs de la réservation
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                //reservation.setCost(Float.parseFloat(costField.getText()));
                reservation.setStatus(Reservation.Status.COMPLETED);
                return reservation;
            }
            return null;
        });

        // Affichage du Dialog et gestion de la réponse
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(updatedReservation -> {
            try {
                reservationService.update(updatedReservation);
                refreshReservations();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred while updating the reservation.");
                alert.setContentText(e.getMessage());

                // Appliquer un style CSS personnalisé pour le message d'erreur
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
                dialogPane.getStyleClass().add("error-alert");

                alert.showAndWait();
            }
        });
    }

    private void handleRefuse(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Refuse Reservation");
        alert.setHeaderText("Are you sure you want to refuse this reservation?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatus(Reservation.Status.CANCELLED);
                reservationService.update(reservation);
                refreshReservations();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("An error occurred while refusing the reservation.");
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