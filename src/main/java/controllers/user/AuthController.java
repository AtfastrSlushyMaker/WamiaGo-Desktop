package controllers.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import entities.User;
import services.AuthService;
import utils.SessionManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AuthController {

    @FXML
    private TextField codeField;

    @FXML
    private Label emailLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private ProgressBar timerProgressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Label resendCodeLabel;

    @FXML
    private Button verifyButton;

    @FXML
    private Button cancelButton;

    private Stage stage;
    private String userEmail;
    private Timeline timeline;
    private Consumer<User> onSuccessCallback;
    private final AuthService twoFactorAuthService = AuthService.getInstance();


    private static final int CODE_EXPIRATION_MINUTES = 10;

    public void initialize() {
        // Clear status label when input changes
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("");

            // Restrict to 6 digits
            if (newValue.length() > 6) {
                codeField.setText(oldValue);
            }

            // Restrict to digits only
            if (!newValue.matches("\\d*")) {
                codeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
        emailLabel.setText(email);
    }

    public void setOnSuccessCallback(Consumer<User> callback) {
        this.onSuccessCallback = callback;
    }

    public void startCountdown() {
        // Initialize countdown timer
        AtomicInteger totalSeconds = new AtomicInteger(CODE_EXPIRATION_MINUTES * 60);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    totalSeconds.getAndDecrement();

                    if (totalSeconds.get() < 0) {
                        // Code has expired
                        timeline.stop();
                        Platform.runLater(() -> {
                            timerLabel.setText("Expired");
                            timerProgressBar.setProgress(0);
                            statusLabel.setText("Verification code has expired. Please request a new code.");
                            verifyButton.setDisable(true);
                        });
                        return;
                    }

                    int minutes = totalSeconds.get() / 60;
                    int seconds = totalSeconds.get() % 60;

                    // Update progress bar
                    double progress = (double) totalSeconds.get() / (CODE_EXPIRATION_MINUTES * 60);

                    Platform.runLater(() -> {
                        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
                        timerProgressBar.setProgress(progress);

                        // Change label color to red when less than 1 minute remains
                        if (minutes < 1) {
                            timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        }
                    });
                })
        );
        timeline.play();
    }

    @FXML
    private void handleVerifyButtonClick(ActionEvent event) {
        String code = codeField.getText().trim();

        // Validate code format
        if (code.isEmpty()) {
            showError("Please enter the verification code.");
            return;
        }

        if (code.length() != 6) {
            showError("Please enter a valid 6-digit code.");
            return;
        }

        // Attempt to verify code
        User authenticatedUser = twoFactorAuthService.verifyCode(userEmail, code);

        if (authenticatedUser != null) {
            // Code verified successfully
            stopCountdown();

            // Call the success callback with the authenticated user
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(authenticatedUser);
            }

            // Close the 2FA window
            stage.close();
        } else {
            showError("Invalid or expired verification code. Please try again.");
        }
    }

    @FXML
    private void handleResendCode(MouseEvent event) {
        // Reset the UI
        statusLabel.setText("");
        codeField.clear();

        // Disable resend button temporarily
        resendCodeLabel.setDisable(true);

        // Stop the current countdown
        stopCountdown();

        // Attempt to send a new code
        if (twoFactorAuthService.generateAndSendCode(getUser())) {
            showSuccess("A new verification code has been sent to your email.");
            startCountdown();

            // Re-enable the resend button after 30 seconds
            Timeline reenableTimeline = new Timeline(new KeyFrame(
                    Duration.seconds(30),
                    e -> resendCodeLabel.setDisable(false)
            ));
            reenableTimeline.play();
        } else {
            showError("Failed to send a new verification code. Please try again.");
            resendCodeLabel.setDisable(false);
        }
    }

    @FXML
    private void handleCancelButtonClick(ActionEvent event) {
        stopCountdown();
        stage.close();
    }

    private void stopCountdown() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
    }

    // Helper method to get the user by email (for resending)
    private User getUser() {
        User tempUser = new User();
        tempUser.setEmail(userEmail);
        return tempUser;
    }
}