package controllers.dashboard;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import controllers.user.ProfileController;
import entities.Location;
import entities.Request;
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
import entities.User;
import controllers.taxi.userside.request.RequestController;

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
    private Button driverspace;

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

    RequestService requestService = new RequestService();

    private void openRequestForm() {
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Taxi");

        VBox modalLayout = new VBox(10);
        modalLayout.getStyleClass().add("modal");
        modalLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("Request Taxi");
        titleLabel.getStyleClass().add("modal-label");

        ComboBox<Location> departureComboBox = new ComboBox<>();
        ComboBox<Location> arrivalComboBox = new ComboBox<>();

        LocationService locationService = new LocationService();
        try {
            List<Location> locations = locationService.read();
            departureComboBox.getItems().setAll(locations);
            arrivalComboBox.getItems().setAll(locations);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VBox comboBoxBox = new VBox(10);
        comboBoxBox.getChildren().addAll(
                new Label("Departure Location:"), departureComboBox,
                new Label("Arrival Location:"), arrivalComboBox
        );

        Button requestTaxiButton = new Button("Request Taxi");
        requestTaxiButton.setOnAction(event -> {
            Location departureLocation = departureComboBox.getValue();
            Location arrivalLocation = arrivalComboBox.getValue();

            if (departureLocation != null && arrivalLocation != null) {
                Request newRequest = new Request();
                newRequest.setDepartureLocation(departureLocation);
                newRequest.setArrivalLocation(arrivalLocation);
                newRequest.setStatus(Request.RequestStatus.PENDING);
                newRequest.setRequestDate(LocalDateTime.now());

                SessionManager sessionManager = SessionManager.getInstance();
                User user = sessionManager.getUser();
                int loggedInUserId = user.getId();

                UserService clientService = new UserService();
                try {
                    User client = clientService.getById(loggedInUserId);
                    newRequest.setClient(client);

                    requestService.create(newRequest);
                    System.out.println("✅ Request created successfully!");
                    // Refresh requests using session-based method


                    modalStage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("⚠️ Please select both departure and arrival locations.");
            }
        });

        modalLayout.getChildren().addAll(titleLabel, comboBoxBox, requestTaxiButton);
        Scene modalScene = new Scene(modalLayout, 350, 250);
        modalScene.getStylesheets().add(getClass().getResource("/taxi-managment/user_side/request.css").toExternalForm());
        modalStage.setScene(modalScene);
        modalStage.show();
    }





    @FXML
    void initialize() throws SQLException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date_label.setText(dateFormat.format(new Date()));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateLabel(dateFormat)),
                new KeyFrame(Duration.seconds(1), e -> updateDateLabel(dateFormat))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        pageNavigation();

        SessionManager sessionManager = SessionManager.getInstance();
        User user = sessionManager.getUser();

        if (user != null) {
            user_name_label.setText(user.getName());
            //manageDashboardByRole(user);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/login.fxml"));
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

    private void updateDateLabel(SimpleDateFormat dateFormat) {
        date_label.setText(dateFormat.format(new Date()));
    }

    protected void pageNavigation() {
        home_button.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                Parent homeRoot = loader.load();
                Scene homeScene = new Scene(homeRoot);
                Stage stage = (Stage) home_button.getScene().getWindow();
                stage.setScene(homeScene);
                pageNavigation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
taxi_request_button.setOnAction(event -> openRequestForm());
        rides_button.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/rides/rides.fxml"));
                Parent ridesRoot = loader.load();
                Scene ridesScene = new Scene(ridesRoot);
                Stage stage = (Stage) rides_button.getScene().getWindow();
                stage.setScene(ridesScene);
                pageNavigation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        logout_button.setOnAction(event -> {
            SessionManager.getInstance().logout();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/login.fxml"));
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
        MenuItem driverSpaceItem = new MenuItem("Driver Space"); // Ajout du bouton

        userMenu.getItems().addAll(profileItem, driverSpaceItem);

        // Action pour le profil
        profileItem.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.back/profile.fxml"));
                Parent profileRoot = loader.load();
                Scene profileScene = new Scene(profileRoot);

                ProfileController profileController = loader.getController();
                profileController.setUserData(SessionManager.getInstance().getUser());

                Stage stage = (Stage) profile_button.getScene().getWindow();
                stage.setScene(profileScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Action pour "Driver Space"
        driverSpaceItem.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/taxi-managment/driver_side/request.fxml"));
                Parent ridesRoot = loader.load();
                Scene ridesScene = new Scene(ridesRoot);

                Stage stage = (Stage) profile_button.getScene().getWindow();
                stage.setScene(ridesScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Afficher le menu au clic sur le bouton
        profile_button.setOnAction(event -> {
            double screenX = profile_button.localToScreen(profile_button.getBoundsInLocal()).getMinX();
            double screenY = profile_button.localToScreen(profile_button.getBoundsInLocal()).getMaxY();
            userMenu.show(profile_button, screenX, screenY);
        });
    }


    private void manageDashboardByRole(User user) throws SQLException {
        User.Role role = user.getRole();
        UserService userService = new UserService();

        switch (role) {
            case ADMIN:
                break;
            case CLIENT:
                break;
        }

       if(userService.isDriver(user)){
           switch (userService.getDriver(user).getDriverRole()){
               case TAXI_DRIVER:

                   break;
               case TRANSPORTER:

                   break;
               case CARPOOL_DRIVER:

                   break;
           }

       }
    }
}

