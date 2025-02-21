package controllers.Announcement;

import entities.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.*;
import utils.SessionManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



public class AnnouncementClientController {
    @FXML
    private ListView<Announcement> announcementListView;

    private final AnnouncementService announcementService = new AnnouncementService();
    private final ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService();
    private final UserService userService = new UserService();

    private User loggedInUser; // Utilisateur connecté

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté
        loggedInUser = SessionManager.getInstance().getUser();

        // Charger les annonces
        announcementListView.getStylesheets().add(getClass().getResource("/Annoucement/front/announcement.css").toExternalForm());
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.read();
            announcementListView.getItems().setAll(announcements);

            // Personnaliser l'affichage des annonces
            announcementListView.setCellFactory(new Callback<>() {
                @Override
                public ListCell<Announcement> call(ListView<Announcement> listView) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(Announcement announcement, boolean empty) {
                            super.updateItem(announcement, empty);
                            if (empty || announcement == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                VBox vbox = new VBox(10);
                                vbox.setPadding(new Insets(10));
                                vbox.getStyleClass().add("announcement-card");

                                Label titleLabel = new Label(announcement.getTitle());
                                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

                                Label contentLabel = new Label(announcement.getContent());
                                contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

                                Label dateLabel = new Label("Date: " + announcement.getDate().toString());
                                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                                Label zoneLabel = new Label("Zone: " + announcement.getZone().toString());
                                zoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                                Button selectButton = new Button("Select");
                                selectButton.getStyleClass().add("select-button");
                                selectButton.setOnAction(event -> openAnnouncementDetails(announcement));

                                Button reserveButton = new Button("Reserve");
                                reserveButton.getStyleClass().add("reserve-button");
                                reserveButton.setOnAction(event -> handleReserveButtonAction(announcement));

                                HBox buttonBox = new HBox(10, selectButton, reserveButton);
                                buttonBox.setAlignment(Pos.CENTER);

                                vbox.getChildren().addAll(titleLabel, contentLabel, dateLabel, zoneLabel, buttonBox);
                                setGraphic(vbox);
                            }
                        }
                    };
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAnnouncementDetails(Announcement announcement) {
        Stage modalStage = new Stage();
        modalStage.setTitle(announcement.getTitle());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px;");

        Label titleLabel = new Label("Details for: " + announcement.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label contentLabel = new Label("Content: " + announcement.getContent());
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        Label dateLabel = new Label("Date: " + announcement.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");

        Label zoneLabel = new Label("Zone: " + announcement.getZone().toString());
        zoneLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("close-button");

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        modalLayout.getChildren().addAll(titleLabel, contentLabel, dateLabel, zoneLabel, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private void handleReserveButtonAction(Announcement announcement) {
        // Création du Dialog pour la réservation
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Reserve Announcement");
        dialog.setHeaderText("Fill in the reservation details");

        // Création des champs de saisie
        DatePicker datePicker = new DatePicker();
        TextField descriptionField = new TextField();
        ComboBox<Station> startLocationComboBox = new ComboBox<>();
        ComboBox<Station> endLocationComboBox = new ComboBox<>();

        // Ajouter une CellFactory pour afficher uniquement l'adresse des stations
        Callback<ListView<Station>, ListCell<Station>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Station> call(ListView<Station> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Station station, boolean empty) {
                        super.updateItem(station, empty);
                        if (empty || station == null) {
                            setText(null);
                        } else {
                            setText(station.getLocation().getAddress());  // Afficher uniquement l'adresse
                        }
                    }
                };
            }
        };

        // Appliquer la CellFactory aux ComboBox
        startLocationComboBox.setCellFactory(cellFactory);
        startLocationComboBox.setButtonCell(cellFactory.call(null));

        endLocationComboBox.setCellFactory(cellFactory);
        endLocationComboBox.setButtonCell(cellFactory.call(null));

        try {
            startLocationComboBox.getItems().addAll(stationService.read());
            endLocationComboBox.getItems().addAll(stationService.read());
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
        ButtonType reserveButtonType = new ButtonType("Reserve", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        // Conversion des résultats en objet Reservation
        dialog.setResultConverter(buttonType -> {
            if (buttonType == reserveButtonType) {
                Reservation reservation = new Reservation();
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                reservation.setDescription(descriptionField.getText());
                reservation.setStartLocation(startLocationComboBox.getValue().getLocation());
                reservation.setEndLocation(endLocationComboBox.getValue().getLocation());
                reservation.setAnnouncement(announcement);

                // Associer l'utilisateur connecté à la réservation
                reservation.setUser(loggedInUser);

                return reservation;
            }
            return null;
        });

        // Affichage du Dialog et gestion de la réponse
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Reservation");
            confirmAlert.setHeaderText("Are you sure you want to reserve this announcement?");
            confirmAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    reservationService.create(reservation);
                    refreshAnnouncements();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred while reserving the announcement.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    private void refreshAnnouncements() {
        announcementListView.getItems().clear();
        loadAnnouncements();
    }
}