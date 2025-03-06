package controllers.dashboard;

import javafx.animation.*;
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
        loadPanels();
        setupInitialVisibility();
        setupNavigationPanelAnimation();

        AnchorPane.setLeftAnchor(contentPane, 0.0);

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

    private void loadPanels() {
        panels.addAll(Arrays.asList(

        ));

        for (AnchorPane panel : panels) {
            contentPane.getChildren().add(panel);

            panel.prefWidthProperty().bind(contentPane.widthProperty());
            panel.prefHeightProperty().bind(contentPane.heightProperty());

            AnchorPane.setTopAnchor(panel, 0.0);
            AnchorPane.setBottomAnchor(panel, 0.0);
            AnchorPane.setLeftAnchor(panel, 0.0);
            AnchorPane.setRightAnchor(panel, 0.0);

            panel.setVisible(false);
        }
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

    private void setupNavigationPanelAnimation() {
        navPanel.setTranslateX(NAV_PANEL_HIDDEN_TRANSLATE);
        navPanel.setOnMouseClicked(event -> toggleNavigationPanel());
    }

    @FXML
    private void menuBar() {
        toggleNavigationPanel();
    }

    private void toggleNavigationPanel() {
        double targetLeftAnchor = (Menu_Counter % 2 == 0) ? 186.0 : 0.0;

        TranslateTransition sidebarTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), navPanel);
        sidebarTransition.setToX((Menu_Counter % 2 == 0) ? 0 : NAV_PANEL_HIDDEN_TRANSLATE);

        Timeline contentTimeline = new Timeline();
        KeyValue keyValue = new KeyValue(contentPane.layoutXProperty(), targetLeftAnchor);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(ANIMATION_DURATION), keyValue);
        contentTimeline.getKeyFrames().add(keyFrame);

        ParallelTransition parallelTransition = new ParallelTransition(sidebarTransition, contentTimeline);
        parallelTransition.setOnFinished(event -> {
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

}