package controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.SessionManager;
import entities.User;

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
        pageNavigation();
        SessionManager sessionManager = SessionManager.getInstance();
        User user = sessionManager.getUser();
        System.out.println(user);
        if (user != null) {
            user_name_label.setText(user.getName());
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/login.fxml"));
                Parent loginRoot = loader.load();
                Scene loginScene = new Scene(loginRoot);
                Stage stage = (Stage) user_name_label.getScene().getWindow();
                stage.setScene(loginScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setupUserDropdownMenu();
    }

    private void pageNavigation() {
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
        logout_button.setOnAction(event -> {
            SessionManager.getInstance().logout();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/login.fxml"));
                Parent loginRoot = loader.load();
                Scene loginScene = new Scene(loginRoot);
                Stage stage = (Stage) logout_button.getScene().getWindow();
                stage.setTitle("Wamia Go - Welcome!");
                stage.setScene(loginScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupUserDropdownMenu() {

        ContextMenu userMenu = new ContextMenu();
        MenuItem profileItem = new MenuItem("Profile");
        userMenu.getItems().addAll(profileItem);

        profile_button.setOnAction(event -> {
            double screenX = profile_button.localToScreen(profile_button.getBoundsInLocal()).getMinX();
            double screenY = profile_button.localToScreen(profile_button.getBoundsInLocal()).getMaxY();

            userMenu.show(profile_button, screenX, screenY);
        });
    }
}
