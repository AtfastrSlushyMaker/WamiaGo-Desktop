package controllers.rides;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class RidesController {
    @FXML
    private Button bookings_button;

    @FXML
    private Button history_button;

    @FXML
    private Button home_button;

    @FXML
    private Button join_button;

    @FXML
    private Button logout_button;

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
    private Button rent_button;

    @FXML
    private Button request_button;

    @FXML
    private Button rides_button;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;
    @FXML
    void initialize() {
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

        request_button.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/taxi-managment/user_side/request.fxml"));
                Parent ridesRoot = loader.load();
                Scene ridesScene = new Scene(ridesRoot);
                Stage stage = (Stage) request_button.getScene().getWindow();
                stage.setScene(ridesScene);
            } catch (IOException e) {

                e.printStackTrace();
            }
        });

        rent_button.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/station/front/station.fxml"));
                Parent ridesRoot = loader.load();
                Scene ridesScene = new Scene(ridesRoot);
                Stage stage = (Stage) rent_button.getScene().getWindow();
                stage.setScene(ridesScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });





    }
}
