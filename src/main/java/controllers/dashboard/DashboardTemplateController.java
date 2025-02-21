    package controllers.dashboard;
    import javafx.animation.TranslateTransition;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Button;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.text.Text;
    import javafx.util.Duration;
    import utils.GeneralFunctions;

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
                //0
                AnchorPane usersPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/user.back/users.fxml")));
                //1
                AnchorPane bikesPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/bicycle.back/bicycle.fxml")));
               //2
                AnchorPane stationsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/station/back/stations.fxml")));
               //3
                AnchorPane rentalsPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/rentals.back/rentals.fxml")));
                panels.addAll(Arrays.asList(
                        usersPanel,
                        bikesPanel,
                        stationsPanel,
                        rentalsPanel
                ));

                for(AnchorPane panel : panels) {
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
            for(int i = 0; i < panels.size(); i++) {
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

        }

        @FXML
        private void usersBtn() {
            showPanel(0);
        }

        @FXML
        public void bicyclesBtn() {showPanel(1);}

        @FXML
        public void stationsBtn() {showPanel(2);}

        @FXML
        public void rentalsBtn() {showPanel(3);}


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