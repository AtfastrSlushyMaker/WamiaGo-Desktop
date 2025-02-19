package controllers.Annoucement;

import controllers.Reservation.ReservationController;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.AnnouncementService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AnnoucementClientController implements Initializable {
    @FXML
    private ListView<Announcement> announcementListView;

    private final AnnouncementService announcementService = new AnnouncementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadAnnouncements();
    }

    private void loadAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.read();
            announcementListView.getItems().setAll(announcements);

            // Customize the display of announcements
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
                                VBox vbox = new VBox();
                                vbox.setSpacing(5);

                                Label titleLabel = new Label(announcement.getTitle());
                                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                                Label contentLabel = new Label(announcement.getContent());
                                contentLabel.setStyle("-fx-font-size: 14px;");

                                Label dateLabel = new Label(announcement.getDate().toString());
                                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                                Label zoneLabel = new Label(announcement.getZone().toString());
                                zoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                                Button reserveButton = new Button("Réserver");
                                reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

                                // Event handler with the correct ActionEvent type
                                reserveButton.setOnAction(event -> handleReserveButtonAction(announcement));

                                vbox.getChildren().addAll(titleLabel, contentLabel, dateLabel, zoneLabel, reserveButton);
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

    @FXML
    private void handleReserveButtonAction(Announcement announcement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/addReservation.fxml"));
            Parent root = loader.load();

            ReservationController controller = loader.getController();
           // controller.setSelectedAnnouncement(announcement);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}