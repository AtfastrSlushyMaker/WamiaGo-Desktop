package entities;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int rideId;
    private ChatListener listener;
    private User user;

    public ChatClient(String ip, int port, int rideId, User user, ChatListener listener) {
        this.rideId = rideId;
        this.user = user;
        this.listener = listener;

        try {
            this.socket = new Socket(ip, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Convert user.getRole() to String
            String role = user.getRole().name();  // If Role is an enum, use name() to get its string representation

            // Send rideId and userRole as String
            writer.write(String.valueOf(rideId));
            writer.newLine();
            writer.write(role);  // Write the role as a string
            writer.newLine();
            writer.flush();

            receiveMessages();
        } catch (IOException e) {
            closeEverything();
        }
    }



    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void receiveMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    listener.onMessageReceived(message);
                }
            } catch (IOException e) {
                closeEverything();
            }
        }).start();
    }

    private void closeEverything() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface ChatListener {
        void onMessageReceived(String message);
    }
}
