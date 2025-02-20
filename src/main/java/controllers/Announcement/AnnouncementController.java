package controllers.Announcement;

import entities.Announcement;
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
import services.AnnouncementService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AnnouncementController implements Initializable {
    @FXML
    private ListView<Announcement> announcementListView;

    @FXML
    private Button btnAdd;

    private final AnnouncementService announcementService = new AnnouncementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnnouncements();

        // Gestionnaire d'événements pour le bouton "Ajouter une annonce"
        btnAdd.setOnAction(event -> handleAddButton());
    }



    private void handleAddButton() {
        try {
            // Charger la vue d'ajout d'annonce
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Annoucement/Front/addAnnouncement.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle vue
            Stage stage = (Stage) btnAdd.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.read();
            announcementListView.getItems().setAll(announcements);

            // Personnaliser l'affichage des annonces
            announcementListView.setCellFactory(new Callback<ListView<Announcement>, ListCell<Announcement>>() {
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
                                // Créer un panneau pour chaque annonce
                                VBox vbox = new VBox();
                                vbox.setSpacing(5);

                                // Titre de l'annonce
                                Label titleLabel = new Label(announcement.getTitle());
                                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                                // Contenu de l'annonce
                                Label contentLabel = new Label(announcement.getContent());
                                contentLabel.setStyle("-fx-font-size: 14px;");

                                // Date de l'annonce
                                Label dateLabel = new Label(announcement.getDate().toString());
                                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                                // Boutons d'action (modifier/supprimer)
                                HBox actionsBox = new HBox();
                                actionsBox.setSpacing(5);

                                Button editButton = new Button("Modifier");
                                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                                editButton.setOnAction(event -> handleEditButtonAction(announcement));


                                Button deleteButton = new Button("Supprimer");
                                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                                // Gestionnaire d'événements pour le bouton "Supprimer"
                                deleteButton.setOnAction(event -> handleDeleteButton(announcement));

                                actionsBox.getChildren().addAll(editButton, deleteButton);

                                vbox.getChildren().addAll(titleLabel, contentLabel, dateLabel, actionsBox);
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

    public void handleEditButtonAction(Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Annoucement/Front/EditAnnouncement.fxml"));
            Parent root = loader.load();

            EditAnnouncementController controller = loader.getController();
            controller.setAnnouncementToEdit(announcement);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleDeleteButton(Announcement announcement) {
        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette annonce ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'annonce de la base de données
                announcementService.delete(announcement.getIdAnnouncement());
                // Recharger la liste des annonces
                loadAnnouncements();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}