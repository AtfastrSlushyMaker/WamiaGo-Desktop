package controllers.dashboard;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DashboardTemplateController implements Initializable {
    @FXML
    private AnchorPane navPanel; // Navigation panel
    @FXML
    private AnchorPane contentPane; // Content area for panels
    @FXML
    private AnchorPane topBar; // Top bar for window controls

    // Constants for navbar animation
    private static final int NAV_PANEL_HIDDEN_TRANSLATE = -186;
    private static final int ANIMATION_DURATION = 400;
    private static int Menu_Counter = 0;

    private List<AnchorPane> panels = new ArrayList<>(); // List to hold all panels
    private double xOffset = 0;
    private double yOffset = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPanels(); // Load all panels
        setupInitialVisibility(); // Show the first panel by default
        setupNavigationPanelAnimation(); // Set up navbar toggle animation


        AnchorPane.setLeftAnchor(contentPane, 0.0);

        // Ensure the navPanel is always on top
        navPanel.setViewOrder(-1);

        // Enable dragging of the window
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
    /**
     * Loads all FXML panels into the content pane and makes them responsive.
     */
    private void loadPanels() {
        try {
            // Load FXML files into panels
            panels.addAll(Arrays.asList(
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.back/users.fxml"))),
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bicycle.back/bicycle.fxml"))),
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/station/back/stations.fxml"))),
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/rentals.back/rentals.fxml"))),
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/request.fxml"))),
                    FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/taxi-managment/admin_side/ride.fxml")))
            ));

            // Add panels to content pane and set visibility
            for (AnchorPane panel : panels) {
                contentPane.getChildren().add(panel);

                // Bind panel size to contentPane size for responsiveness
                panel.prefWidthProperty().bind(contentPane.widthProperty());
                panel.prefHeightProperty().bind(contentPane.heightProperty());

                // Set layout constraints
                AnchorPane.setTopAnchor(panel, 0.0);
                AnchorPane.setBottomAnchor(panel, 0.0);
                AnchorPane.setLeftAnchor(panel, 0.0);
                AnchorPane.setRightAnchor(panel, 0.0);

                panel.setVisible(false); // Hide all panels initially
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load FXML files", e);
        }
    }
    /**
     * Shows the specified panel and hides all others.
     *
     * @param panelIndex The index of the panel to show.
     */
    @FXML
    private void showPanel(int panelIndex) {
        if (panelIndex < 0 || panelIndex >= panels.size()) {
            throw new IllegalArgumentException("Invalid panel index: " + panelIndex);
        }
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setVisible(i == panelIndex);
        }
    }

    /**
     * Sets up the initial visibility of the first panel.
     */
    private void setupInitialVisibility() {
        if (!panels.isEmpty()) {
            panels.get(0).setVisible(true); // Show the first panel by default
        }
    }

    /**
     * Sets up the navbar toggle animation and adjusts the content pane layout.
     */
    private void setupNavigationPanelAnimation() {
        navPanel.setTranslateX(NAV_PANEL_HIDDEN_TRANSLATE);
        navPanel.setOnMouseClicked(event -> toggleNavigationPanel());
    }

    /**
     * Toggles the navbar visibility and adjusts the content pane layout.
     */
    @FXML
    private void menuBar() {
        toggleNavigationPanel();
    }

    private void toggleNavigationPanel() {
        double targetLeftAnchor = (Menu_Counter % 2 == 0) ? 186.0 : 0.0;

        // Animate the sidebar
        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
        sidebarTransition.setToX((Menu_Counter % 2 == 0) ? 0 : NAV_PANEL_HIDDEN_TRANSLATE);

        // Animate the contentPane's left anchor
        Timeline contentTimeline = new Timeline();
        KeyValue keyValue = new KeyValue(contentPane.layoutXProperty(), targetLeftAnchor);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(ANIMATION_DURATION), keyValue);
        contentTimeline.getKeyFrames().add(keyFrame);

        // Play both animations simultaneously
        ParallelTransition parallelTransition = new ParallelTransition(sidebarTransition, contentTimeline);
        parallelTransition.setOnFinished(event -> {
            // Update the contentPane's left anchor after the animation completes
            AnchorPane.setLeftAnchor(contentPane, targetLeftAnchor);
        });
        parallelTransition.play();

        Menu_Counter++;
    }
    @FXML
    private void handleMinimizeButton(ActionEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.setIconified(true); // Minimize the window
    }

    @FXML
    private void handleMaximizeButton(ActionEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        if (stage.isMaximized()) {
            stage.setMaximized(false); // Restore the window
        } else {
            stage.setMaximized(true); // Maximize the window
        }
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.close(); // Close the window
    }

    // Panel button handlers
    @FXML
    private void usersBtn() { showPanel(0); }
    @FXML
    public void bicyclesBtn() { showPanel(1); }
    @FXML
    public void stationsBtn() { showPanel(2); }
    @FXML
    public void rentalsBtn() { showPanel(3); }
    @FXML
    public void taxiRequests() { showPanel(4); }
    @FXML
    public void taxiRides() { showPanel(5); }
    @FXML
    private void AccountSettingsBtn() { showPanel(1); }
    @FXML
    private void DashboardBtn() { showPanel(1); }
    @FXML
    private void QueriesBtn() { showPanel(6); }

    @FXML
    private void handleTopBarDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Check for double-click
            Stage stage = (Stage) topBar.getScene().getWindow();
            if (stage.isMaximized()) {
                stage.setMaximized(false); // Restore the window
            } else {
                stage.setMaximized(true); // Maximize the window
            }
        }
    }

    // Logout button handler (to be implemented)
    @FXML
    private void logoutBtn(ActionEvent event) throws IOException {
        // Perform logout logic here (e.g., clear session, redirect to login page)
        Stage stage = (Stage) topBar.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.front/loginSignup.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }
}