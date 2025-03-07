package utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class EmailSender {

    private static final String EMAIL = "abrouguiazer1920@gmail.com";
    private static final String PASSWORD = "ickv tniu okzz sayx";

    public void sendEmail(String toEmail, String emailContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL, PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Nouvelle réservation de transport");

            // Créer une partie multipart pour le contenu HTML et l'image
            MimeMultipart multipart = new MimeMultipart("related");

            // Partie HTML
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(emailContent, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Partie image
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource("src/main/resources/images/logo/wamiaGO.png"); // Chemin de l'image
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<logo>"); // Utiliser le même cid que dans le HTML
            multipart.addBodyPart(messageBodyPart);

            // Ajouter le multipart au message
            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email envoyé avec succès à " + toEmail + "!");
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}