package controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DashboardController {
        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private Button bicycle_rent_button;

        @FXML
        private Button booking_button;

        @FXML
        private Button carpool_join_button;

        @FXML
        private Label date_label;

        @FXML
        private Button history_button;

        @FXML
        private Button home_button;

        @FXML
        private Button logout_button;

        @FXML
        private Pane pane_11;

        @FXML
        private Pane pane_111;

        @FXML
        private Pane pane_1111;

        @FXML
        private Pane pane_112;

        @FXML
        private Pane pane_1121;

        @FXML
        private Pane pane_131;

        @FXML
        private Button profile_button;

        @FXML
        private Button reclamation_button;

        @FXML
        private Button relocation_reserve_button;

        @FXML
        private Button rides_button;

        @FXML
        private HBox root;

        @FXML
        private AnchorPane side_ankerpane;

        @FXML
        private Button taxi_request_button;

        @FXML
        private Label user_name_label;

        @FXML
        void initialize() {
            rides_button.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/rides/rides.fxml"));
                    Parent ridesRoot = loader.load();
                    Scene ridesScene = new Scene(ridesRoot);
                    Stage stage = (Stage) rides_button.getScene().getWindow();
                    stage.setScene(ridesScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            home_button.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                    Parent homeRoot = loader.load();
                    Scene homeScene = new Scene(homeRoot);
                    Stage stage = (Stage) home_button.getScene().getWindow();
                    stage.setScene(homeScene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


        public void setUser_name_label(String user_name) {
            user_name_label.setText(user_name);
        }



}
