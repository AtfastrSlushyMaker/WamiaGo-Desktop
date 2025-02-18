package controllers.Reservation;

import entities.Driver;
import entities.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.ReservationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    @FXML
    private ListView<Reservation> reservationListView;

    @FXML


    private final ReservationService reservationService = new ReservationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadReservations();


    }



    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.read();
            reservationListView.getItems().setAll(reservations);

            // Personnaliser l'affichage des réservations
            reservationListView.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
                @Override
                public ListCell<Reservation> call(ListView<Reservation> listView) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(Reservation reservation, boolean empty) {
                            super.updateItem(reservation, empty);
                            if (empty || reservation == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                // Créer un panneau pour chaque réservation
                                VBox vbox = new VBox();
                                vbox.setSpacing(5);

                                // Date de la réservation
                                Label dateLabel = new Label("Date: " + reservation.getDate().toString());
                                dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                                // Statut de la réservation
                                Label statusLabel = new Label("Statut: " + reservation.getStatus().toString());
                                statusLabel.setStyle("-fx-font-size: 14px;");

                                // Description de la réservation
                                Label descriptionLabel = new Label("Description: " + reservation.getDescription());
                                descriptionLabel.setStyle("-fx-font-size: 14px;");

                                // Lieux de départ et d'arrivée
                                Label locationsLabel = new Label("De " + reservation.getStartLocation().getAddress() + " à " + reservation.getEndLocation().getAddress());
                                locationsLabel.setStyle("-fx-font-size: 14px;");

                                // Annonce associée
                                Label announcementLabel = new Label("Annonce: " + reservation.getAnnouncement().getTitle());
                                announcementLabel.setStyle("-fx-font-size: 14px;");

                                // Transporteur associé
                                Label carrierLabel;
                                Driver transporter = reservation.getAnnouncement().getTransporter();
                                if (transporter != null) {
                                    carrierLabel = new Label("Transporteur: " + transporter.getIdDriver());
                                } else {
                                    carrierLabel = new Label("Transporteur: Non défini");
                                }

                              //  Label carrierLabel = new Label("Transporteur: " + reservation.getAnnouncement().getTransporter().getIdDriver());
                                carrierLabel.setStyle("-fx-font-size: 14px;");

                                // Boutons d'action (modifier/supprimer)
                                HBox actionsBox = new HBox();
                                actionsBox.setSpacing(5);

                                Button editButton = new Button("Modifier");
                                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                                editButton.setOnAction(event -> handleEditButtonAction(reservation));

                                Button deleteButton = new Button("Supprimer");
                                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                                deleteButton.setOnAction(event -> handleDeleteButton(reservation));

                                actionsBox.getChildren().addAll(editButton, deleteButton);

                                vbox.getChildren().addAll(dateLabel, statusLabel, descriptionLabel, locationsLabel, announcementLabel, carrierLabel, actionsBox);
                                setGraphic(vbox);
                            }
                        }
                    };
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleEditButtonAction(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/EditReservation.fxml"));
            Parent root = loader.load();

            EditReservationController controller = loader.getController();
            controller.setReservationToEdit(reservation);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que la fenêtre de modification soit fermée

            // Rafraîchir la liste des réservations après modification
            loadReservations();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteButton(Reservation reservation) {
        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer la réservation de la base de données
                reservationService.delete(reservation.getIdReservation());
                // Recharger la liste des réservations
                loadReservations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}