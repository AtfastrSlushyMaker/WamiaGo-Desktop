package controllers;

import entities.Announcement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addAnnouncement.fxml"));
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

                                Button deleteButton = new Button("Supprimer");
                                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

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
}