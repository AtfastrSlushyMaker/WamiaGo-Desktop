package controllers;

import entities.Announcement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

    private final AnnouncementService announcementService = new AnnouncementService();

//    @FXML
//    private Button btnAdd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnnouncements();

//        // Gestionnaire d'événements pour le bouton "Ajouter une annonce"
//        btnAdd.setOnAction(event -> {
//            try {
//                // Charger la vue d'ajout d'annonce
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/addAnnouncement.fxml"));
//                Parent root = loader.load();
//
//                // Obtenir la scène actuelle et la remplacer par la nouvelle vue
//                Stage stage = (Stage) btnAdd.getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
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
                            } else {
                                setText(announcement.getTitle() + " - " + announcement.getZone() + " (" + announcement.getDate() + ")");
                                setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 5px;");
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
