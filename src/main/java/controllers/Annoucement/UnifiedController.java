package controllers.Annoucement;


import controllers.Reservation.AddReservationController;
import entities.Announcement;
import entities.Reservation;
import entities.Location;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.AnnouncementService;
import services.ReservationService;
import services.LocationService;
import services.UserService;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class UnifiedController {
    @FXML
    private Button home_button;
    @FXML
    private Button rides_button;
    @FXML
    private FlowPane announcementFlowPane; // Pour afficher les annonces
    @FXML
    private FlowPane reservationFlowPane; // Pour afficher les réservations

    private final AnnouncementService announcementService = new AnnouncementService();
    private final ReservationService reservationService = new ReservationService();
    private final LocationService locationService = new LocationService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadAnnouncementsIntoFlowPane();
        loadReservationsIntoFlowPane();
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
        announcementCard.getStyleClass().add("announcement-card");
        announcementCard.setAlignment(Pos.CENTER);

        // Titre de l'annonce
        Label titleLabel = new Label(announcement.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Contenu de l'annonce
        Label contentLabel = new Label(announcement.getContent());
        contentLabel.setStyle("-fx-font-size: 14px;");

        // Date de l'annonce
        Label dateLabel = new Label("Date: " + announcement.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label zoneLabel = new Label("Date: " + announcement.getZone().toString());
        zoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Bouton de réservation
        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        reserveButton.setOnAction(event -> handleReserveButtonAction(announcement));

        // Ajouter les éléments à la carte
        announcementCard.getChildren().addAll(titleLabel, contentLabel, dateLabel,zoneLabel, reserveButton);

        // Effet de survol
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

    private void handleReserveButtonAction(Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/addReservation.fxml"));
            Parent root = loader.load();

            AddReservationController controller = loader.getController();
            controller.setSelectedAnnouncement(announcement);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservationsIntoFlowPane() {
        try {
            for (Reservation reservation : reservationService.read()) {
                VBox reservationCard = createReservationCard(reservation);
                reservationFlowPane.getChildren().add(reservationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox reservationCard = new VBox(10);
        reservationCard.setPadding(new Insets(10));
        reservationCard.getStyleClass().add("reservation-card");
        reservationCard.setAlignment(Pos.CENTER);

        // ID de la réservation
        Label idLabel = new Label("Reservation ID: " + reservation.getIdReservation());
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Date de la réservation
        Label dateLabel = new Label("Date: " + reservation.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px;");

        // Statut de la réservation
        Label statusLabel = new Label("Status: " + reservation.getStatus());
        statusLabel.setStyle("-fx-font-size: 14px;");

        // Bouton pour afficher les détails
        Button detailsButton = new Button("Détails");
        detailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        detailsButton.setOnAction(event -> showReservationDetails(reservation));

        // Ajouter les éléments à la carte
        reservationCard.getChildren().addAll(idLabel, dateLabel, statusLabel, detailsButton);

        // Effet de survol
        reservationCard.setOnMouseEntered(event -> {
            reservationCard.setScaleX(1.05);
            reservationCard.setScaleY(1.05);
        });

        reservationCard.setOnMouseExited(event -> {
            reservationCard.setScaleX(1);
            reservationCard.setScaleY(1);
        });

        return reservationCard;
    }

    private void showReservationDetails(Reservation reservation) {
        // Créer une fenêtre modale pour afficher les détails de la réservation
        Stage modalStage = new Stage();
        modalStage.setTitle("Détails de la réservation");

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");

        Label titleLabel = new Label("Détails de la réservation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label idLabel = new Label("ID: " + reservation.getIdReservation());
        idLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Label dateLabel = new Label("Date: " + reservation.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Label statusLabel = new Label("Statut: " + reservation.getStatus());
        statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Label descriptionLabel = new Label("Statut: " + reservation.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(event -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

        modalLayout.getChildren().addAll(titleLabel, idLabel, dateLabel, statusLabel, closeButton);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }
}