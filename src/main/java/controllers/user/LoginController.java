package controllers.user;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import entities.User;
import entities.User.Gender;
import entities.User.Role;
import services.UserService;
import utils.sessionManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class LoginController {
    @FXML private TextField signupEmailField;
    @FXML private TextField signupFirstNameField;
    @FXML private TextField signupLastNameField;
    @FXML private TextField signupPhoneField;
    @FXML private DatePicker signupDatePicker;
    @FXML private RadioButton maleRadioButton;
    @FXML private RadioButton femaleRadioButton;
    @FXML private PasswordField signupPasswordField;
    @FXML private PasswordField signupConfirmPasswordField;
    @FXML private Button signupButton;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @FXML private Pane slidingPanel;
    @FXML private Pane signupPane;
    @FXML private Pane loginPane;
    @FXML private Button switchFormButton;

    @FXML private ToggleGroup genderGroup;

    private final UserService userService = new UserService();
    private boolean isSignUpVisible = false;

    @FXML
    private void handleSignUpButtonClick() throws SQLException {
        if (validateSignUpForm()) {
            User newUser = new User();
            newUser.setEmail(signupEmailField.getText());
            newUser.setName(signupFirstNameField.getText() + " " + signupLastNameField.getText());
            newUser.setPhone(signupPhoneField.getText());
            newUser.setDateOfBirth(signupDatePicker.getValue());
            newUser.setGender(maleRadioButton.isSelected() ? Gender.MALE : Gender.FEMALE);
            newUser.setPassword(signupPasswordField.getText());
            newUser.setRole(Role.CLIENT);
            newUser.setVerified(false);
            newUser.setAccountStatus(User.AccountStatus.ACTIVE);
            newUser.setStatus(User.Status.OFFLINE);

            if (userService.create(newUser)) {
                showAlert("Sign-Up Successful", "Welcome, " + newUser.getName() + "!");
                clearSignUpForm();
            } else {
                showAlert("Error", "User registration failed. Try again.");
            }
        } else {
            showAlert("Validation Error", "Please fill in all fields correctly.");
        }
    }

    @FXML
    private void handleLoginButtonClick() throws SQLException {
        if (validateLoginForm()) {
            String email = emailField.getText();
            String password = passwordField.getText();

            User authenticatedUser = userService.authenticateUser(email, password);
            if (authenticatedUser != null) {
                sessionManager.getInstance().setUser(authenticatedUser);
                showAlert("Login Successful", "Welcome back, " + authenticatedUser.getName() + "!");
                clearLoginForm();
                //loadMainApp();
            } else {
                showAlert("Login Failed", "Invalid credentials. Try again.");
            }
        } else {
            showAlert("Validation Error", "Please enter a valid email and password.");
        }
    }
//#####################ANIMATION##############################
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
        transition.setToX(460);
        transition.play();
    }

    private void slidePanelToLeft() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), slidingPanel);
        transition.setToX(0);
        transition.play();
    }
//###########################################################
    private boolean validateSignUpForm() {
        return !signupEmailField.getText().isEmpty() &&
                !signupFirstNameField.getText().isEmpty() &&
                !signupLastNameField.getText().isEmpty() &&
                !signupPhoneField.getText().isEmpty() &&
                signupDatePicker.getValue() != null &&
                (maleRadioButton.isSelected() || femaleRadioButton.isSelected()) &&
                !signupPasswordField.getText().isEmpty() &&
                signupPasswordField.getText().equals(signupConfirmPasswordField.getText());
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
    }

    private void clearLoginForm() {
        emailField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}