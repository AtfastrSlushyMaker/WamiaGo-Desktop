package controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class LoginController {

    // Sign-Up Section
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

    // Login Section
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    // Sliding Panel and Form Switching
    @FXML private Pane slidingPanel;
    @FXML private Pane signupPane;
    @FXML private Pane loginPane;
    @FXML private Button switchFormButton;

    // ToggleGroup for Gender Selection
    @FXML private ToggleGroup genderGroup;

    // Track the current form state
    private boolean isSignUpVisible = false;

    // Initialize Method (Optional)
    @FXML
    public void initialize() {
        // Set up any initial configurations here
        System.out.println("Controller initialized!");
    }

    // Handle Sign-Up Button Click
    @FXML
    private void handleSignUpButtonClick() {
        // Validate input fields
        if (validateSignUpForm()) {
            // Process sign-up data
            String email = signupEmailField.getText();
            String firstName = signupFirstNameField.getText();
            String lastName = signupLastNameField.getText();
            String phone = signupPhoneField.getText();
            String dob = signupDatePicker.getValue() != null ? signupDatePicker.getValue().toString() : "No Date Selected";
            String gender = maleRadioButton.isSelected() ? "Male" : femaleRadioButton.isSelected() ? "Female" : "No Gender Selected";
            String password = signupPasswordField.getText();

            // Print sign-up data (for demonstration)
            System.out.println("Sign-Up Data:");
            System.out.println("Email: " + email);
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);
            System.out.println("Phone: " + phone);
            System.out.println("Date of Birth: " + dob);
            System.out.println("Gender: " + gender);
            System.out.println("Password: " + password);

            // Clear the form after submission
            clearSignUpForm();

            // Show success message
            showAlert("Sign-Up Successful", "Welcome, " + firstName + "!");
        } else {
            showAlert("Validation Error", "Please fill in all fields correctly.");
        }
    }

    // Handle Login Button Click
    @FXML
    private void handleLoginButtonClick() {
        // Validate input fields
        if (validateLoginForm()) {
            // Process login data
            String email = emailField.getText();
            String password = passwordField.getText();

            // Print login data (for demonstration)
            System.out.println("Login Data:");
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);

            // Clear the form after submission
            clearLoginForm();

            // Show success message
            showAlert("Login Successful", "Welcome back!");
        } else {
            showAlert("Validation Error", "Please enter a valid email and password.");
        }
    }

    // Handle Switch Form Button Click
    @FXML
    private void handleSwitchFormButtonClick() {
        // Toggle between login and sign-up forms
        if (isSignUpVisible) {
            slidePanelToLeft();
            switchFormButton.setText("Sign Up");
        } else {
            slidePanelToRight();
            switchFormButton.setText("Log In");
        }
        isSignUpVisible = !isSignUpVisible;
    }

    // Slide Panel to the Right (Show Sign-Up Form)
    private void slidePanelToRight() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), slidingPanel);
        transition.setToX(460); // Slide to the right
        transition.play();
    }

    // Slide Panel to the Left (Show Login Form)
    private void slidePanelToLeft() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), slidingPanel);
        transition.setToX(0); // Slide to the left
        transition.play();
    }

    // Validate Sign-Up Form
    private boolean validateSignUpForm() {
        if (signupEmailField.getText().isEmpty() ||
                signupFirstNameField.getText().isEmpty() ||
                signupLastNameField.getText().isEmpty() ||
                signupPhoneField.getText().isEmpty() ||
                signupDatePicker.getValue() == null ||
                (!maleRadioButton.isSelected() && !femaleRadioButton.isSelected()) ||
                signupPasswordField.getText().isEmpty() ||
                signupConfirmPasswordField.getText().isEmpty()) {
            return false; // Missing fields
        }

        // Check if passwords match
        if (!signupPasswordField.getText().equals(signupConfirmPasswordField.getText())) {
            showAlert("Password Mismatch", "Passwords do not match.");
            return false;
        }

        return true; // All fields are valid
    }

    // Validate Login Form
    private boolean validateLoginForm() {
        return !emailField.getText().isEmpty() && !passwordField.getText().isEmpty();
    }

    // Clear Sign-Up Form
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

    // Clear Login Form
    private void clearLoginForm() {
        emailField.clear();
        passwordField.clear();
    }

    // Show Alert Dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}