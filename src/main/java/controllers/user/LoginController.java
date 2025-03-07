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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import entities.User;
import entities.User.Gender;
import entities.User.Role;
import services.AuthService;
import services.LocationService;
import services.UserService;
import utils.SessionManager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class LoginController {
    @FXML
    public ProgressIndicator loadingIndicator;
    public TextField signupFirstNameField;
    public AnchorPane topBar;
    public AnchorPane contentPane;
    public AnchorPane rootPane;
    public StackPane loadingStackPane;
    public CheckBox rememberMeCheckBox;
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
        loadSavedCredentials(); // Load saved email and checkbox state from ini file if it exists
    }

    private void loadSavedCredentials() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("user.ini")) {
            properties.load(reader);
            String savedEmail = properties.getProperty("email");
            boolean rememberMe = Boolean.parseBoolean(properties.getProperty("rememberMe", "false"));

            if (savedEmail != null && !savedEmail.isEmpty()) {
                emailField.setText(savedEmail);
            }
            rememberMeCheckBox.setSelected(rememberMe);
        } catch (IOException e) {
            // File might not exist yet, which is fine
        }
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
            boolean rememberMe = rememberMeCheckBox.isSelected();

            loadingIndicator.setVisible(true); // Show loading indicator

            new Thread(() -> {
                try {
                    User authenticatedUser = userService.authenticateUser(email, password);
                    Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        if (authenticatedUser != null) {
                            if (rememberMe) {
                                saveUserPreferences(email, rememberMe);
                            } else {
                                resetBypass2FA();
                            }

                            if (should2FABeBypassed(email)) {
                                // Bypass 2FA and directly log in
                                completeLoginProcess(authenticatedUser);
                            } else {
                                // Proceed with 2FA
                                initiateTwoFactorAuthentication(authenticatedUser, rememberMe);
                            }
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
    }private void resetBypass2FA() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("user.ini")) {
            properties.load(reader);
        } catch (IOException e) {
            // If file doesn't exist, nothing to reset
            return;
        }

        // Reset the bypass2fa property
        properties.setProperty("bypass2fa", "false");

        try (FileWriter writer = new FileWriter("user.ini")) {
            properties.store(writer, "User Login Information");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean should2FABeBypassed(String email) {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("user.ini")) {
            properties.load(reader);
            String savedEmail = properties.getProperty("email", "");
            String bypass2FA = properties.getProperty("bypass2fa", "false");

            // Only bypass if the current email matches the saved one and bypass2fa is true
            return savedEmail.equals(email) && "true".equals(bypass2FA);
        } catch (IOException e) {
            // If file doesn't exist or can't be read, don't bypass
            return false;
        }
    }
    private void saveUserPreferences(String email, boolean rememberMe) {
        Properties properties = new Properties();

        try (FileReader reader = new FileReader("user.ini")) {
            // Load existing properties if file exists
            properties.load(reader);
        } catch (IOException e) {
            // If file doesn't exist, we'll create a new one
        }

        // Update properties
        properties.setProperty("email", email);
        properties.setProperty("bypass2fa", String.valueOf(rememberMe));
        properties.setProperty("rememberMe", String.valueOf(rememberMe));

        try (FileWriter writer = new FileWriter("user.ini")) {
            properties.store(writer, "User Login Information");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getEmailFromIniFile() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("user.ini")) {
            properties.load(reader);
            return properties.getProperty("email");
        } catch (IOException e) {
            // File might not exist yet, which is fine
            return null;
        }
    }

    private void completeLoginProcess(User authenticatedUser) {
        SessionManager.getInstance().setUser(authenticatedUser);
        authenticatedUser.setStatus(User.Status.ONLINE);

        showAlert("Login Successful", "Welcome back, " + authenticatedUser.getName() + "!");
        clearLoginForm();
        loadDashboard();
    }

    private void initiateTwoFactorAuthentication(User user, boolean rememberMe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/2FA.fxml"));
            Parent root = loader.load();

            AuthController authController = loader.getController();
            authController.setUserEmail(user.getEmail());
            authController.setOnSuccessCallback(authenticatedUser -> {
                if (rememberMe) {
                    saveBypass2FA(user.getEmail());
                }
                completeLoginProcess(authenticatedUser);
            });

            Stage stage = new Stage();
            stage.setTitle("Two-Factor Authentication");
            stage.setScene(new Scene(root));
            authController.setStage(stage);
            authController.startCountdown();
            AuthService.getInstance().generateAndSendCode(user);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the 2FA window.");
            e.printStackTrace();
        }
    }

    private void saveBypass2FA(String email) {
        Properties properties = new Properties();

        try (FileReader reader = new FileReader("user.ini")) {
            // Load existing properties if file exists
            properties.load(reader);
        } catch (IOException e) {
            // If file doesn't exist, we'll create a new one
        }

        // Update properties
        properties.setProperty("email", email);
        properties.setProperty("bypass2fa", "true");

        try (FileWriter writer = new FileWriter("user.ini")) {
            properties.store(writer, "User Login Information");
        } catch (IOException e) {
            e.printStackTrace();
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
        rememberMeCheckBox.setSelected(false);
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

    @FXML
    public void openForgetPWD(MouseEvent mouseEvent) {
        try {
            // Load the FXML file for the forget password modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/ForgetPassword.fxml"));
            Parent root = loader.load();

            // Create a new stage for the modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Reset Password");
            modalStage.initModality(Modality.APPLICATION_MODAL); // Make the modal block other windows
            modalStage.setScene(new Scene(root));

            // Get the controller for the modal
            ForgetPasswordController controller = loader.getController();
            controller.setModalStage(modalStage);

            // Show the modal and wait for it to be closed
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the reset password modal.");
        }
    }
}