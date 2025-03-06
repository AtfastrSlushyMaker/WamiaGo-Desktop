package controllers.Chat_Relocation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entities.Message;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatController {

    @FXML
    private TextArea messageArea;

    @FXML
    private ListView<Message> chatListView;

    @FXML
    private Label toEmailLabel;

    private String fromEmail ;
    private String toEmail ;


    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    private static final String BASE_URL = "http://localhost:8081/api/messages";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() throws IOException {


        if (toEmailLabel != null) {
            toEmailLabel.setText(toEmail);
            refreshChatList();
        }


        chatListView.setCellFactory(new ChatMessageCellFactory(fromEmail, this));
        chatListView.getItems().addListener((ListChangeListener<Message>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    scrollToBottom();
                }
            }
        });

        refreshChatList();

    }

    @FXML
    private void sendMessage() {
        String content = messageArea.getText().trim();


        if (content.isEmpty()) {
            System.out.println("Please enter a message.");
            return;
        }

        try {

            String response = sendMessageToServer(fromEmail, toEmail, content);
            System.out.println("Message sent: " + response);
            System.out.println("Email to : " + toEmail);
            System.out.println("fromEmail : " + fromEmail);
            refreshChatList();
            messageArea.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendMessageToServer(String fromEmail, String toEmail, String content) throws IOException {
        // Encoder les paramètres pour éviter les caractères non valides dans l'URL
        String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
        String url = BASE_URL + "/send?fromEmail=" + fromEmail + "&toEmail=" + toEmail + "&content=" + encodedContent;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new IOException("Failed to send message: " + response.getStatusLine().getReasonPhrase());
            }
        }
    }

    public void refreshChatList() throws IOException {
        String url = BASE_URL + "/chatwith?meEmail=" + fromEmail + "&otherEmail=" + toEmail;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = client.execute(httpGet);
        String jsonResponse = EntityUtils.toString(response.getEntity());
        List<Message> messages = objectMapper.readValue(jsonResponse, new TypeReference<List<Message>>() {});
        chatListView.getItems().clear();
        chatListView.getItems().addAll(messages);

        scrollToBottom();
    }

    private void scrollToBottom() {
        if (chatListView != null && !chatListView.getItems().isEmpty()) {
            int lastIndex = chatListView.getItems().size() - 1;
            chatListView.scrollTo(lastIndex);
            chatListView.getSelectionModel().select(lastIndex);
        }
    }
}