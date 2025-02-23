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
        root.getStylesheets().add(getClass().getResource("Annoucement/Front/announcementclient.css").toExternalForm());
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
        announcementCard.getStyleClass().add("announcement-card");
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

        ImageView announcementImage = new ImageView(new Image(getClass().getResource("/images/icons/announcement.png").toExternalForm()));
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

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Title: " + announcement.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label contentLabel = new Label("Content: " + announcement.getContent());
        contentLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label dateLabel = new Label("Date: " + announcement.getDate());
        dateLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label zoneLabel = new Label("Zone: " + announcement.getZone());
        zoneLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label statusLabel = new Label("Status: " + (announcement.getStatus() ? "Active" : "Inactive"));
        statusLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("announcement-close-button");

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll(titleLabel, contentLabel, dateLabel, zoneLabel, statusLabel, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void handleReserve(Announcement announcement) {
        // Création du formulaire pour l'ajout d'une réservation
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Reserve Announcement");
        dialog.setHeaderText("Enter reservation details");

        // Création des champs de saisie
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField descriptionField = new TextField();

        ComboBox<String> startLocationComboBox = new ComboBox<>();
        ComboBox<String> endLocationComboBox = new ComboBox<>();

        try {
            List<Location> locations = locationService.read();
            for (Location location : locations) {
                startLocationComboBox.getItems().add(location.getAddress());
                endLocationComboBox.getItems().add(location.getAddress());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajout des champs au Dialog
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

        dialog.getDialogPane().setContent(grid);

        // Ajout des boutons OK et Annuler
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Conversion des résultats en objet Reservation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                // Vérifier que tous les champs sont remplis
                if (datePicker.getValue() == null || descriptionField.getText().isEmpty() ||
                        startLocationComboBox.getValue() == null || endLocationComboBox.getValue() == null) {
                    // Afficher un message d'erreur si un champ est vide
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }

                // Créer la réservation
                Reservation reservation = new Reservation();
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                reservation.setDescription(descriptionField.getText());

                try {
                    // Récupérer les objets Location à partir des adresses sélectionnées
                    Location startLocation = locationService.getByAddress(startLocationComboBox.getValue());
                    Location endLocation = locationService.getByAddress(endLocationComboBox.getValue());
                    reservation.setStartLocation(startLocation);
                    reservation.setEndLocation(endLocation);

                    // Associer l'annonce à la réservation
                    reservation.setAnnouncement(announcement);


//                    Driver transporter=new DriverService().getById(announcement.getTransporter().getIdDriver());
//
//                    reservation.
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return reservation;
            }
            return null;
        });

        // Affichage du Dialog et gestion de la réponse
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            try {
                // Enregistrer la réservation dans la base de données
                reservationService.create(reservation);
                refreshAnnouncements();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occurred while creating the reservation.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void refreshAnnouncements() {
        announcementFlowPane.getChildren().clear();
        loadAnnouncementsIntoFlowPane();
    }
}