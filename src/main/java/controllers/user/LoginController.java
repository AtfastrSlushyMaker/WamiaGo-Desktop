package controllers.user;

import entities.Location;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import entities.User;
import entities.User.Gender;
import entities.User.Role;
import services.LocationService;
import services.UserService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LoginController {
    @FXML
    public ProgressIndicator loadingIndicator;
    public TextField signupFirstNameField;
    public AnchorPane topBar;
    public AnchorPane contentPane;
    public AnchorPane rootPane;
    public StackPane loadingStackPane;
    @FXML
    private TextField signupEmailField;
    @FXML
    private TextField signupLastNameField;
    @FXML
    private TextField signupPhoneField;
    @FXML
    private DatePicker signupDatePicker;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private ComboBox<Location> signupLocationComboBox;
    @FXML
    private RadioButton femaleRadioButton;
    @FXML
    private PasswordField signupPasswordField;
    @FXML
    private PasswordField signupConfirmPasswordField;
    @FXML
    private Button signupButton;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    private Pane slidingPanel;
    @FXML
    private Pane signupPane;
    @FXML
    private Pane loginPane;
    @FXML
    private Button switchFormButton;

    @FXML
    private ToggleGroup genderGroup;

    private final UserService userService = new UserService();
    private final LocationService locationService = new LocationService();
    private boolean isSignUpVisible = false;
    @FXML
    public void initialize() {
        loadLocations(); // Load locations into the ComboBox
        setupFormSwitching(); // Set up the sliding animation for form switching
        makeResponsive(); // Make the layout responsive
    }
    private void makeResponsive() {
        // Bind the width of the signupPane and loginPane to the contentPane's width
        signupPane.prefWidthProperty().bind(contentPane.widthProperty().divide(2));
        loginPane.prefWidthProperty().bind(contentPane.widthProperty().divide(2));

        // Bind the height of the signupPane and loginPane to the contentPane's height
        signupPane.prefHeightProperty().bind(contentPane.heightProperty());
        loginPane.prefHeightProperty().bind(contentPane.heightProperty());

        // Bind the sliding panel's width to the contentPane's width divided by 2
        slidingPanel.prefWidthProperty().bind(contentPane.widthProperty().divide(2));

        // No need to bind the loading indicator's position, as it's centered by the StackPane
    }

    private void loadLocations() {
        try {
            List<Location> locations = locationService.read();
            signupLocationComboBox.getItems().setAll(locations);
        } catch (SQLException e) {
            showAlert("Database Error", "Could not load locations.");
        }
    }

    @FXML
    private void handleSignUpButtonClick() {
        if (validateSignUpForm()) {
            User newUser = createUserFromForm();
            if (newUser != null) {
                try {
                    if (userService.create(newUser)) {
                        showAlert("Sign-Up Successful", "Welcome, " + newUser.getName() + "!");
                        clearSignUpForm();
                    } else {
                        showAlert("Error", "User registration failed. Try again.");
                    }
                } catch (SQLException e) {
                    showAlert("Database Error", "Could not register user.");
                }
            }
        } else {
            showAlert("Validation Error", "Please fill in all fields correctly.");
        }
    }

    private User createUserFromForm() {
        User newUser = new User();
        newUser.setEmail(signupEmailField.getText());
        newUser.setName(signupFirstNameField.getText() + " " + signupLastNameField.getText());
        newUser.setPhone(signupPhoneField.getText());
        newUser.setDateOfBirth(signupDatePicker.getValue());
        newUser.setGender(maleRadioButton.isSelected() ? Gender.MALE : Gender.FEMALE);
        newUser.setPassword(signupPasswordField.getText());
        newUser.setLocation(signupLocationComboBox.getValue());
        newUser.setRole(Role.CLIENT);
        newUser.setVerified(false);
        newUser.setAccountStatus(User.AccountStatus.ACTIVE);
        newUser.setStatus(User.Status.OFFLINE);
        return newUser;
    }

    @FXML
    private void handleLoginButtonClick() {
        if (validateLoginForm()) {
            String email = emailField.getText();
            String password = passwordField.getText();

            loadingIndicator.setVisible(true); // Show loading indicator

            new Thread(() -> {
                try {
                    User authenticatedUser = userService.authenticateUser(email, password);
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false); // Hide loading indicator
                        if (authenticatedUser != null) {
                            SessionManager.getInstance().setUser(authenticatedUser);
                            authenticatedUser.setStatus(User.Status.ONLINE);
                            showAlert("Login Successful", "Welcome back, " + authenticatedUser.getName() + "!");
                            clearLoginForm();
                            loadDashboard();
                        } else {
                            showAlert("Login Failed", "Invalid credentials. Try again.");
                        }
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false); // Hide loading indicator
                        showAlert("Database Error", "Could not authenticate user.");
                    });
                }
            }).start();
        } else {
            showAlert("Validation Error", "Please enter a valid email and password.");
        }
    }

    private void loadDashboard() {
        String fxmlPath = SessionManager.getInstance().getUser().getRole() == Role.ADMIN ? "/dashboard/dashboardAdmin.fxml" : "/dashboard/dashboard.fxml";
        loadingIndicator.setVisible(true); // Show loading indicator

        new Thread(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Platform.runLater(() -> {
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setTitle("Dashboard");
                    stage.setScene(new Scene(root));
                    stage.show();
                });
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "Failed to load the dashboard."));
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> loadingIndicator.setVisible(false)); // Hide loading indicator
            }
        }).start();
    }

    @FXML
    private void handleSwitchFormButtonClick() {
        if (isSignUpVisible) {
            slidePanelToLeft();
            switchFormButton.setText("Sign Up");
        } else {
            slidePanelToRight();
            switchFormButton.setText("Log In");
        }
        isSignUpVisible = !isSignUpVisible;
    }

    private void slidePanelToRight() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), slidingPanel);
        transition.setToX(signupPane.getWidth());
        transition.play();
    }

    private void slidePanelToLeft() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), slidingPanel);
        transition.setToX(0);
        transition.play();
    }

    private boolean validateSignUpForm() {
        return !signupEmailField.getText().isEmpty() &&
                !signupFirstNameField.getText().isEmpty() &&
                !signupLastNameField.getText().isEmpty() &&
                !signupPhoneField.getText().isEmpty() &&
                signupDatePicker.getValue() != null &&
                (maleRadioButton.isSelected() || femaleRadioButton.isSelected()) &&
                !signupPasswordField.getText().isEmpty() &&
                signupPasswordField.getText().equals(signupConfirmPasswordField.getText()) &&
                signupLocationComboBox.getValue() != null;
    }

    private boolean validateLoginForm() {
        return !emailField.getText().isEmpty() && !passwordField.getText().isEmpty();
    }

    private void clearSignUpForm() {
        signupEmailField.clear();
        signupFirstNameField.clear();
        signupLastNameField.clear();
        signupPhoneField.clear();
        signupDatePicker.setValue(null);
        genderGroup.selectToggle(null);
        signupPasswordField.clear();
        signupConfirmPasswordField.clear();
        signupLocationComboBox.getSelectionModel().clearSelection();
    }

    private void clearLoginForm() {
        emailField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void setupFormSwitching() {
        slidingPanel.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (isSignUpVisible) {
                slidePanelToRight();
            } else {
                slidePanelToLeft();
            }
        });
    }


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
        stage.close();
    }

}