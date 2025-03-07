package services;

import entities.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class AuthService {

    private static AuthService instance;
    private final Map<String, TwoFactorAuthCode> authCodes = new HashMap<>();

    // Expiration time in minutes
    private static final int CODE_EXPIRATION_MINUTES = 10;

    // Private constructor for singleton pattern
    private AuthService() {}

    // Singleton instance getter
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    // Inner class to store code and expiration time
    public static class TwoFactorAuthCode {
        private final String code;
        private final LocalDateTime expirationTime;
        private final User user;

        public TwoFactorAuthCode(String code, User user) {
            this.code = code;
            this.user = user;
            this.expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

        public User getUser() {
            return user;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }

    /**
     * Generates and sends a 2FA code to the user's email
     * @param user The user to generate 2FA code for
     * @return true if code was sent successfully, false otherwise
     */
    public boolean generateAndSendCode(User user) {
        String code = generateVerificationCode();
        if (sendVerificationEmail(user.getEmail(), code)) {
            // Store the code with the user's email as key
            authCodes.put(user.getEmail(), new TwoFactorAuthCode(code, user));
            return true;
        }
        return false;
    }

    /**
     * Verifies a 2FA code for a given email
     * @param email The user's email
     * @param code The 2FA code entered by the user
     * @return The user if verification successful, null otherwise
     */
    public User verifyCode(String email, String code) {
        TwoFactorAuthCode authCode = authCodes.get(email);

        // Check if code exists and is not expired
        if (authCode != null && !authCode.isExpired() && authCode.getCode().equals(code)) {
            // Remove the code from the map after successful verification
            authCodes.remove(email);
            return authCode.getUser();
        }

        // Code invalid or expired
        if (authCode != null && authCode.isExpired()) {
            // Remove expired code
            authCodes.remove(email);
        }

        return null;
    }

    /**
     * Generates a random 6-digit verification code
     * @return A 6-digit code as string
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Sends a verification email with the 2FA code
     * @param email The recipient's email
     * @param code The verification code
     * @return true if email sent successfully, false otherwise
     */
    private boolean sendVerificationEmail(String email, String code) {
        String password = System.getenv("GOOGLE_APP_PWD");
        if (password == null || password.isEmpty()) {
            System.err.println("Email service configuration error: GOOGLE_APP_PWD not set");
            return false;
        }

        final String username = "rzouga.psn@gmail.com";
        final String senderName = "WamiaGo Security";

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
            message.setSubject("Two-Factor Authentication Code");

            // Calculate expiration time in human-readable format
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
            String expirationTimeString = String.format("%02d:%02d",
                    expirationTime.getHour(),
                    expirationTime.getMinute());

            // HTML email template
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
                    + ".security-note { background-color: #fff8e1; border-left: 4px solid #ffc107; padding: 15px; margin-top: 20px; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<div class='header'>"
                    + "<h1>WamiaGo Two-Factor Authentication</h1>"
                    + "</div>"
                    + "<div class='content'>"
                    + "<p class='greeting'>Hello,</p>"
                    + "<p class='message'>For your account security, we need to verify it's really you. Please use the following code to complete your login:</p>"
                    + "<div class='code-container'>"
                    + "<p class='verification-code'>" + code + "</p>"
                    + "<p class='expiration'>This code will expire in " + CODE_EXPIRATION_MINUTES + " minutes (at " + expirationTimeString + ")</p>"
                    + "</div>"
                    + "<p class='message'>If you did not attempt to login to your WamiaGo account, please change your password immediately.</p>"
                    + "<div class='security-note'>"
                    + "<p><strong>Security Tip:</strong> Never share this code with anyone. WamiaGo representatives will never ask for your verification code.</p>"
                    + "</div>"
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
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}