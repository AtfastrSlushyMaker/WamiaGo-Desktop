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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardUserController implements Initializable {
    public AnchorPane contentPane;
    public Button home_button;
    public Button rides_button;
    public Label date_label;
    public Button booking_button;
    public Button history_button;
    public Text Username;
    public Button menuBarBtn;
    public Button restoreBtn;
    public Button maxBtn;
    public Button closeBtn;
    public Button logout;
    public Button Settings;
    @FXML
    private AnchorPane navPanel;
    @FXML
    private AnchorPane topBar;

    private static final int NAV_PANEL_HIDDEN_TRANSLATE = -300;
    private static final int ANIMATION_DURATION = 400;
    private static int Menu_Counter = 0;

    private List<AnchorPane> panels = new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navPanel.prefHeightProperty().bind(contentPane.heightProperty());

        navPanel.prefHeightProperty().bind(contentPane.heightProperty());

        User user = SessionManager.getInstance().getUser();
        Username.setText(user.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date_label.setText(dateFormat.format(new Date()));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateLabel(dateFormat)),
                new KeyFrame(Duration.seconds(1), e -> updateDateLabel(dateFormat))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        try {
            loadPanels();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setupInitialVisibility();
        setupNavigationPanelAnimation();

        AnchorPane.setLeftAnchor(contentPane, 186.0); // Set initial left anchor to 186.0

        navPanel.setViewOrder(-1);

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

    private void setupNavigationPanelAnimation() {
        navPanel.setTranslateX(0); // Set initial translation to 0
        navPanel.setOnMouseClicked(event -> toggleNavigationPanel());
    }

    private void toggleNavigationPanel() {
        boolean isMenuOpen = (Menu_Counter % 2 == 0);
        double targetLeftAnchor = isMenuOpen ? 186.0 : 0.0;

        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
        sidebarTransition.setToX(isMenuOpen ? 0 : NAV_PANEL_HIDDEN_TRANSLATE);

        ParallelTransition parallelTransition = new ParallelTransition(sidebarTransition);
        parallelTransition.setOnFinished(event -> {
            AnchorPane.setLeftAnchor(contentPane, targetLeftAnchor);
            contentPane.prefWidthProperty().bind(contentPane.getScene().widthProperty().subtract(targetLeftAnchor));
        });
        parallelTransition.play();

        Menu_Counter++;
    }

    private void loadPanels() throws IOException {
        Platform.runLater(() -> {
            try {
                panels.addAll(Arrays.asList(
                        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/dashboard/home.fxml"))),
                        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/rides/rides.fxml")))
                ));

                for (AnchorPane panel : panels) {
                    contentPane.getChildren().add(panel);

                    // Bind panel size to contentPane
                    panel.prefWidthProperty().bind(contentPane.widthProperty());
                    panel.prefHeightProperty().bind(contentPane.heightProperty());

                    // Ensure panels are aligned to contentPane
                    AnchorPane.setTopAnchor(panel, 0.0);
                    AnchorPane.setBottomAnchor(panel, 0.0);
                    AnchorPane.setLeftAnchor(panel, 0.0);
                    AnchorPane.setRightAnchor(panel, 0.0);

                    panel.setVisible(false); // Set initial visibility to false
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void showPanel(int panelIndex) {
        if (panelIndex < 0 || panelIndex >= panels.size()) {
            throw new IllegalArgumentException("Invalid panel index: " + panelIndex);
        }
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setVisible(i == panelIndex);
        }
    }

    private void setupInitialVisibility() {
        if (!panels.isEmpty()) {
            panels.get(0).setVisible(true);
        }
    }

    @FXML
    private void menuBar() {
        toggleNavigationPanel();
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
            stage.setMaximized(false);
        } else {
            stage.setMaximized(true);
        }
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Stage stage = (Stage) topBar.getScene().getWindow();
        stage.close(); // Close the window
    }


    @FXML
    private void handleTopBarDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Stage stage = (Stage) topBar.getScene().getWindow();
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        }
    }
    
    @FXML
    private void logoutBtn(ActionEvent event) throws IOException {
        Stage stage = (Stage) topBar.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.front/loginSignup.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void updateDateLabel(SimpleDateFormat dateFormat) {
        date_label.setText(dateFormat.format(new Date()));
    }
    public void homeBtn(ActionEvent actionEvent) {
        showPanel(0);
    }
    public void ridesBtn(ActionEvent actionEvent) {
        showPanel(1);
    }


}