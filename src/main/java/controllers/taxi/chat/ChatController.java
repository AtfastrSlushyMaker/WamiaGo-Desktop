package controllers.taxi.chat;

import entities.ChatClient;
import entities.Ride;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController implements ChatClient.ChatListener {
    @FXML private TextField messageField;
    @FXML private ListView<String> chatListView;

    private ChatClient chatClient;
    private Ride currentRide;
    private User user;  // Declare the User field
    private String userType;

    public void initChat(Ride ride, User user) {
        this.currentRide = ride;
        this.user = user;  // Store the User object

        // Initialisation du client de chat avec l'ID de la course et l'utilisateur
        chatClient = new ChatClient("127.0.0.1", 1234, ride.getIdRide(), user, this);
    }

    @FXML
    public void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatClient.sendMessage(message);
            chatListView.getItems().add("Moi : " + message);
            messageField.clear();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        chatListView.getItems().add(message);
    }
}
