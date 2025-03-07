package controllers.dashboard;

import controllers.user.ProfileController;
import entities.Location;
import entities.Request;
import entities.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.LocationService;
import services.RequestService;
import services.UserService;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController {
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
    private Button driverspace;

    @FXML
    private Button reclamation_button;

    @FXML
    private Button relocation_reserve_button;

    @FXML
    private Button rides_button;

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private Button taxi_request_button;


    @FXML
    void initialize() throws SQLException {


    }}


