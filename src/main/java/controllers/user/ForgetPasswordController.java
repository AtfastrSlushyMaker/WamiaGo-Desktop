package controllers.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.UserService;
import entities.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ForgetPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField verificationCodeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private VBox step1Container;

    @FXML
    private VBox step2Container;

    private String verificationCode;
    private LocalDateTime codeExpirationTime;
    private Stage modalStage;
    private UserService userService = new UserService();
    private Timeline timeline;

    // Expiration time in minutes
    private static final int CODE_EXPIRATION_MINUTES = 15;

    public void setModalStage(Stage modalStage) {
        this.modalStage = modalStage;
    }

    public void initialize() {
        // Initially hide the timer label
        timerLabel.setVisible(false);

        // Clear the status label when user types in any field
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("");
        });

        verificationCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("");
        });

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("");
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setText("");
        });
    }

    @FXML
    private void handleSendCodeButtonClick(ActionEvent event) {
        String email = emailField.getText();
        if (email.isEmpty()) {
            showError("Please enter your email address.");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }

        statusLabel.setText("Verifying email...");

        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                showError("No account found with this email address.");
                return;
            }

            // Generate code and set expiration time
            verificationCode = generateVerificationCode();
            codeExpirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

            // Start countdown timer
            startCountdownTimer();

            if (sendVerificationEmail(email, verificationCode)) {
                showSuccess("Verification code sent! Check your email inbox.");
                timerLabel.setVisible(true);
            } else {
                showError("Failed to send verification code. Please try again.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error occurred. Please try again later.");
        }
    }

    @FXML
    private void handleResetPasswordButtonClick(ActionEvent event) {
        String enteredCode = verificationCodeField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic field validation
        if (enteredCode.isEmpty()) {
            showError("Please enter the verification code.");
            return;
        }

        if (newPassword.isEmpty()) {
            showError("Please enter a new password.");
            return;
        }

        if (confirmPassword.isEmpty()) {
            showError("Please confirm your new password.");
            return;
        }

        // Check if verification code has expired
        if (codeExpirationTime == null || LocalDateTime.now().isAfter(codeExpirationTime)) {
            showError("Verification code has expired. Please request a new code.");
            stopCountdownTimer();
            return;
        }

        // Validate passwords
        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Password strength check
        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters long.");
            return;
        }

        // Validate verification code
        if (!enteredCode.equals(verificationCode)) {
            showError("Invalid verification code. Please check and try again.");
            return;
        }

        // Update the user's password in the database
        String email = emailField.getText();
        try {
            User user = userService.getUserByEmail(email);
            if (user != null) {
                userService.updatePassword(user.getEmail(),newPassword);
                showSuccess("Password successfully reset!");

                stopCountdownTimer();

                // Close the modal after 2 seconds
                Timeline closeTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> modalStage.close()));
                closeTimeline.play();
            } else {
                showError("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error. Please try again later.");
        }
    }

    @FXML
    private void handleCancelButtonClick(ActionEvent event) {
        stopCountdownTimer();
        modalStage.close();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void startCountdownTimer() {
        // Stop existing timer if running
        stopCountdownTimer();

        // Start a new countdown timer
        AtomicInteger totalSeconds = new AtomicInteger(CODE_EXPIRATION_MINUTES * 60);
        timeline = new Timeline();
        timeline.setCycleCount(totalSeconds.get());
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    totalSeconds.getAndDecrement();
                    int minutes = totalSeconds.get() / 60;
                    int seconds = totalSeconds.get() % 60;
                    timerLabel.setText(String.format("Code expires in: %02d:%02d", minutes, seconds));

                    // Change color to red when less than 2 minutes remain
                    if (minutes < 2) {
                        timerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }

                    if (totalSeconds.get() <= 0) {
                        timerLabel.setText("Code expired! Request a new code.");
                        timeline.stop();
                    }
                })
        );
        timeline.play();
    }

    private void stopCountdownTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: #fdecea; -fx-padding: 10; -fx-background-radius: 4;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-background-color: #eafaf1; -fx-padding: 10; -fx-background-radius: 4;");
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean sendVerificationEmail(String email, String code) {
        String password = System.getenv("GOOGLE_APP_PWD");
        if (password == null || password.isEmpty()) {
            showError("Email service configuration error.");
            return false;
        }

        final String username = "rzouga.psn@gmail.com"; // Replace with your Gmail address
        final String senderName = "WamiaGo Support"; // Sender name

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, senderName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset Verification Code");

            // Calculate expiration time in human-readable format
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
            String expirationTimeString = String.format("%02d:%02d",
                    expirationTime.getHour(),
                    expirationTime.getMinute());

            // Enhanced HTML email template
            String htmlContent = "<html>"
                    + "<head>"
                    + "<style type='text/css'>"
                    + "body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f7f9fc; margin: 0; padding: 0; }"
                    + ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }"
                    + ".header { background-color: #4285f4; padding: 25px; text-align: center; }"
                    + ".header h1 { color: #ffffff; margin: 0; font-weight: 500; font-size: 24px; }"
                    + ".content { padding: 30px; color: #333333; }"
                    + ".greeting { font-size: 18px; margin-bottom: 15px; }"
                    + ".code-container { background-color: #f0f4f9; border-radius: 6px; padding: 25px; margin: 25px 0; text-align: center; border-left: 4px solid #4285f4; }"
                    + ".verification-code { font-family: 'Courier New', monospace; font-size: 32px; font-weight: bold; color: #4285f4; letter-spacing: 5px; margin: 0; }"
                    + ".expiration { color: #e74c3c; font-size: 14px; margin-top: 10px; font-weight: 500; }"
                    + ".message { line-height: 1.6; }"
                    + ".footer { background-color: #f0f4f9; padding: 20px; text-align: center; font-size: 14px; color: #666666; }"
                    + ".button { display: inline-block; background-color: #4285f4; color: #ffffff; text-decoration: none; padding: 12px 25px; border-radius: 4px; font-weight: 500; margin-top: 15px; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<div class='header'>"
                    + "<h1>WamiaGo Password Reset</h1>"
                    + "</div>"
                    + "<div class='content'>"
                    + "<p class='greeting'>Hello,</p>"
                    + "<p class='message'>We received a request to reset your password for your WamiaGo account. To proceed with the password reset, please use the verification code below:</p>"
                    + "<div class='code-container'>"
                    + "<p class='verification-code'>" + code + "</p>"
                    + "<p class='expiration'>This code will expire in " + CODE_EXPIRATION_MINUTES + " minutes (at " + expirationTimeString + ")</p>"
                    + "</div>"
                    + "<p class='message'>If you did not request this password reset, please ignore this email or contact support if you have concerns.</p>"
                    + "<p class='message'>Once verified, you'll be able to create a new password for your account.</p>"
                    + "<p class='message'>Best regards,<br>The WamiaGo Team</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>This is an automated message, please do not reply directly to this email.</p>"
                    + "<p>&copy; " + java.time.Year.now().getValue() + " WamiaGo. All rights reserved.</p>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            // Set the email content as HTML
            message.setContent(htmlContent, "text/html");

            Transport.send(message);
            return true;
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            showError("Failed to send email: " + e.getMessage());
            return false;
        }
    }
}