package controllers.dashboard;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DashboardTemplateController implements Initializable {
    @FXML
    private AnchorPane navPanel;
    @FXML
    private AnchorPane contentPane;
    private static final int NAV_PANEL_HIDDEN_TRANSLATE = -186;
    private static final int ANIMATION_DURATION = 400;

    private static int Menu_Counter = 0;

    private List<AnchorPane> panels = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPanels();
        setupInitialVisibility();
        setupNavigationPanelAnimation();
    }

    private void loadPanels() {
        try {
            // Existing panels
            AnchorPane usersPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.back/users.fxml")));
            AnchorPane bikesPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane stationsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane rentalsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane requestsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane ridesPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/ride.fxml")));
            AnchorPane responsesPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane announcementsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane reservationsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));
            AnchorPane relocationsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml")));

            // Use the same FXML for Taxi Stats
            AnchorPane statisticsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/StatisticsRequestRide.fxml")));

            // Add all panels including the new Taxi Stats panel
            panels.addAll(Arrays.asList(
                    usersPanel,
                    bikesPanel,
                    stationsPanel,
                    rentalsPanel,
                    requestsPanel,
                    ridesPanel,
                    responsesPanel,
                    announcementsPanel,
                    reservationsPanel,
                    relocationsPanel,
                    statisticsPanel  // Add the Taxi Stats panel here
            ));

            // Setting visibility of panels
            for (AnchorPane panel : panels) {
                contentPane.getChildren().add(panel);
                AnchorPane.setTopAnchor(panel, 0.0);
                AnchorPane.setBottomAnchor(panel, 0.0);
                AnchorPane.setLeftAnchor(panel, 0.0);
                AnchorPane.setRightAnchor(panel, 0.0);
                panel.setVisible(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML files", e);
        }
    }

    @FXML
    private void showPanel(int panelIndex) {
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setVisible(i == panelIndex);
        }
    }

    private void setupInitialVisibility() {
        if (!panels.isEmpty() && panels.size() > 0) {
            panels.get(0).setVisible(true);
        }
    }

    private void setupNavigationPanelAnimation() {
        navPanel.setTranslateX(NAV_PANEL_HIDDEN_TRANSLATE);
        navPanel.setOnMouseClicked(event -> {
            TranslateTransition transition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
            if (Menu_Counter % 2 == 0) {
                transition.setToX(0);
            } else {
                transition.setToX(NAV_PANEL_HIDDEN_TRANSLATE);
            }
            transition.play();
            Menu_Counter++;
        });
    }

    @FXML
    private void menuBar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
        if (Menu_Counter % 2 == 0) {
            transition.setToX(0);
        } else {
            transition.setToX(NAV_PANEL_HIDDEN_TRANSLATE);
        }
        transition.play();
        Menu_Counter++;
    }

    @FXML
    private void logoutBtn(ActionEvent e) throws IOException {
        // Implement logout functionality
    }

    @FXML
    private void usersBtn() {
        showPanel(0);
    }

    @FXML
    public void bicyclesBtn() {
        showPanel(1);
    }

    @FXML
    public void stationsBtn() {
        showPanel(2);
    }

    @FXML
    public void rentalsBtn() {
        showPanel(3);
    }

    @FXML
    public void taxiRequests() {
        showPanel(4);
    }

    @FXML
    public void taxiRides() {
        showPanel(5);
    }

    @FXML
    public void responsesBtn() {
        showPanel(6);
    }

    @FXML
    public void announcementsBtn() {
        showPanel(7);
    }

    @FXML
    public void reservationsBtn() {
        showPanel(8);
    }

    @FXML
    public void relocationsBtn() {
        showPanel(9);
    }

    @FXML
    public void statisticsBtn() {
        showPanel(10);
    }

    @FXML
    private void taxiStatisticsBtn(ActionEvent event) {
        // Handle the button click here, for example:
        System.out.println("Taxi Statistics button clicked!");
        // You can also load a new scene or show the taxi statistics
        showPanel(10);
    }

    @FXML
    private void AccountSettingsBtn() {
        showPanel(1);
    }

    @FXML
    private void DashboardBtn() {
        showPanel(1);
    }

    @FXML
    private void QueriesBtn() {
        showPanel(6);
    }

    @FXML
    private void close() {
        System.exit(0);
    }

    @FXML
    private void max() {
        // Maximize action
        // Example:
        // Stage stage = (Stage) navPanel.getScene().getWindow();
        // stage.setMaximized(true);
    }

    @FXML
    private void restore() {
        // Restore action
        // Example:
        // Stage stage = (Stage) navPanel.getScene().getWindow();
        // stage.setMaximized(false);
    }
}
