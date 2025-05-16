package controllers.dashboard;

import entities.User;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardAdminController implements Initializable {
    @FXML private AnchorPane contentPane;
    @FXML private AnchorPane navPanel;
    @FXML private AnchorPane topBar;
    @FXML private Text Username;
    @FXML private Label date_label;

    private static final int NAV_PANEL_HIDDEN_TRANSLATE = -300;
    private static final int ANIMATION_DURATION = 400;
    private static int Menu_Counter = 0;

    private final List<Pane> panels = new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize user session and date
        User user = SessionManager.getInstance().getUser();
        Username.setText(user.getName());
        updateDateLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // Setup date update timeline
        setupDateUpdater();

        // Load all panels
        loadAllPanels();

        // Setup UI behaviors
        setupNavigationPanel();
        setupWindowDragBehavior();
    }

    private void setupDateUpdater() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        e -> updateDateLabel(dateFormat))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadAllPanels() {
        List<String> fxmlPaths = Arrays.asList(
                "/Statistics.Front/Statistics.fxml",           // Panel 0 - Dashboard
                "/taxi-managment/admin_side/StatisticsRequestRide.fxml", // Panel 1 - Statistics
                "/user.back/users.fxml",                       // Panel 2 - Users
                "/taxi-managment/admin_side/request.fxml",     // Panel 3 - Taxi Requests
                "/rentals.back/rentals.fxml",                  // Panel 4 - Rentals
                "/station/back/stations.fxml",                 // Panel 5 - Stations
                "/bicycle.back/bicycle.fxml",                  // Panel 6 - Bicycles
                "/taxi-managment/admin_side/ride.fxml",        // Panel 7 - Taxi Rides
                "/trips/back_trip.fxml",                       // Panel 8 - Carpooling
                "/Reclamation/ListReclamationBack.fxml",       // Panel 9 - Feedbacks
                "/Annoucement/Front/announcementAdmin.fxml"    // Panel 10 - Announcements
        );

        for (String path : fxmlPaths) {
            try {
                Pane panel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
                setupPanel(panel);
                panels.add(panel);
            } catch (IOException | NullPointerException e) {
                System.err.println("Failed to load panel: " + path);
                e.printStackTrace();

                panels.add(new Pane());
            }
        }

        // Show default panel (Dashboard)
        if (!panels.isEmpty()) {
            Platform.runLater(() -> showPanel(0));
        }
    }

    private void setupPanel(Pane panel) {
        panel.setVisible(false);
        contentPane.getChildren().add(panel);

        // Bind panel size to contentPane
        panel.prefWidthProperty().bind(contentPane.widthProperty());
        panel.prefHeightProperty().bind(contentPane.heightProperty());

        // Anchor panel to all sides of contentPane
        AnchorPane.setTopAnchor(panel, 0.0);
        AnchorPane.setBottomAnchor(panel, 0.0);
        AnchorPane.setLeftAnchor(panel, 0.0);
        AnchorPane.setRightAnchor(panel, 0.0);
    }

    private void showPanel(int panelIndex) {
        System.out.println("Attempting to show panel: " + panelIndex);

        if (panelIndex < 0 || panelIndex >= panels.size()) {
            System.out.println("Invalid panel index: " + panelIndex);
            return;
        }

        // Hide all panels first
        panels.forEach(panel -> panel.setVisible(false));

        // Show the selected panel
        Pane panel = panels.get(panelIndex);
        panel.setVisible(true);
        System.out.println("Panel " + panelIndex + " visibility: " + panel.isVisible());
    }

    private void setupNavigationPanel() {
        navPanel.setTranslateX(0);
        AnchorPane.setLeftAnchor(contentPane, 186.0);
        navPanel.setViewOrder(-1);
    }

    private void setupWindowDragBehavior() {
        topBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void toggleNavigationPanel() {
        double targetLeftAnchor = (Menu_Counter % 2 == 0) ? 0.0 : 186.0;

        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
        sidebarTransition.setToX((Menu_Counter % 2 == 0) ? NAV_PANEL_HIDDEN_TRANSLATE : 0);

        Timeline contentTimeline = new Timeline();
        KeyValue keyValue = new KeyValue(contentPane.layoutXProperty(), targetLeftAnchor);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(ANIMATION_DURATION), keyValue);
        contentTimeline.getKeyFrames().add(keyFrame);

        ParallelTransition parallelTransition = new ParallelTransition(sidebarTransition, contentTimeline);
        parallelTransition.setOnFinished(event -> AnchorPane.setLeftAnchor(contentPane, targetLeftAnchor));
        parallelTransition.play();

        Menu_Counter++;
    }

    private void updateDateLabel(SimpleDateFormat dateFormat) {
        Platform.runLater(() -> date_label.setText(dateFormat.format(new Date())));
    }

    // Window control handlers
    @FXML private void menuBar() { toggleNavigationPanel(); }
    @FXML private void handleMinimizeButton(ActionEvent event) { ((Stage) topBar.getScene().getWindow()).setIconified(true); }
    @FXML private void handleMaximizeButton(ActionEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
    @FXML private void handleCloseButton(ActionEvent event) { ((Stage) topBar.getScene().getWindow()).close(); }
    @FXML private void handleTopBarDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) handleMaximizeButton(null);
    }

    @FXML private void logoutBtn(ActionEvent event) throws IOException {
        Stage stage = (Stage) topBar.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.front/loginSignup.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML public void handleDashboardBtn() { showPanel(0); }        // Dashboard
    @FXML public void handleStatisticsBtn() { showPanel(1); }       // Statistics
    @FXML public void handleUsersBtn() { showPanel(2); }            // Users
    @FXML public void handleTaxiRequests() { showPanel(3); }        // Taxi Requests
    @FXML public void handleRentalsBtn() { showPanel(4); }          // Rentals
    @FXML public void handleStationsBtn() { showPanel(5); }         // Stations
    @FXML public void handleBicyclesBtn() { showPanel(6); }         // Bicycles
    @FXML public void handleTaxiRides() { showPanel(7); }           // Taxi Rides
    @FXML public void handleCarpoolingBtn() { showPanel(8); }       // Carpooling
    @FXML public void handleQueriesBtn() { showPanel(9); }          // Feedbacks
    @FXML public void handleAnnouncementsBtn() { showPanel(10); }   // Announcements
}