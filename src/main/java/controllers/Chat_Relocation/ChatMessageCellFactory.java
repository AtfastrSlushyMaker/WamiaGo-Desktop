package controllers.Chat_Relocation;

import entities.Message;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class ChatMessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {

    private final String currentUserEmail; // Store the current user's email
    private final ChatController chatController; // Reference to the controller

    public ChatMessageCellFactory(String currentUserEmail, ChatController chatController) {
        this.currentUserEmail = currentUserEmail;
        this.chatController = chatController;
    }

    @Override
    public ListCell<Message> call(ListView<Message> param) {
        return new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a TextFlow for the message content
                    Text text = new Text(message.getContent());
                    text.setFill(Color.WHITE);
                    text.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

                    // Create a Text for the timestamp
                    Text timestampText = new Text("\n" + message.getTimestamp().toString());
                    timestampText.setFill(Color.LIGHTGRAY);
                    timestampText.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

                    // Combine content and timestamp in a TextFlow
                    TextFlow textFlow = new TextFlow(text, timestampText);
                    textFlow.setMaxWidth(200); // Set max width for the bubble
                    textFlow.setStyle("-fx-background-color: " + (message.getFromEmail().equals(currentUserEmail) ? "#4CAF50" : "#2196F3") + "; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 10;");

                    // Create an HBox to align the message bubble
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);

                    // Align to the right if the message is from the current user, otherwise to the left
                    if (message.getFromEmail().equals(currentUserEmail)) {
                        hbox.setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        hbox.setAlignment(Pos.CENTER_LEFT);
                    }

                    hbox.getChildren().add(textFlow);
                    setGraphic(hbox);

                    // Add a context menu for deleting messages (only for messages sent by the current user)
                    if (message.getFromEmail().equals(currentUserEmail)) {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem deleteItem = new MenuItem("Delete");
                        deleteItem.setOnAction(event -> {
                            try {
                                deleteMessage(message.getId());
                                chatController.refreshChatList(); // Refresh the chat list after deletion
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        contextMenu.getItems().add(deleteItem);

                        // Set the context menu on the cell
                        setContextMenu(contextMenu);
                    } else {
                        // No context menu for other users' messages
                        setContextMenu(null);
                    }
                }
            }
        };
    }

    private void deleteMessage(Long messageId) throws IOException {
        String url = "http://localhost:8081/api/messages/delete/" + messageId;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        HttpResponse response = client.execute(httpDelete);

        if (response.getStatusLine().getStatusCode() == 200) {
            System.out.println("Message deleted successfully");
        } else {
            System.out.println("Failed to delete message");
        }
    }
}