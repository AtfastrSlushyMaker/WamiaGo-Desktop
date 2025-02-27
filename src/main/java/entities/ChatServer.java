package entities;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private ServerSocket serverSocket;
    private static Map<Integer, ChatSession> chatSessions = new HashMap<>();

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Nouvelle connexion !");
                    ClientHandler clientHandler = new ClientHandler(socket);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                closeServer();
            }
        }).start();
    }

    private void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ChatSession {
        private ClientHandler client;
        private ClientHandler driver;

        public void setClient(ClientHandler client) {
            this.client = client;
        }

        public void setDriver(ClientHandler driver) {
            this.driver = driver;
        }

        public void sendMessage(String senderType, String message) {
            if (senderType.equals("client") && driver != null) {
                driver.sendMessage("Client : " + message);
            } else if (senderType.equals("driver") && client != null) {
                client.sendMessage("Chauffeur : " + message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private int rideId;
        private String userType;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // Lire rideId et userType dès la connexion
                this.rideId = Integer.parseInt(reader.readLine());
                this.userType = reader.readLine(); // "client" ou "driver"

                chatSessions.putIfAbsent(rideId, new ChatSession());

                if (userType.equals("client")) {
                    chatSessions.get(rideId).setClient(this);
                } else if (userType.equals("driver")) {
                    chatSessions.get(rideId).setDriver(this);
                }
            } catch (IOException e) {
                closeEverything();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    chatSessions.get(rideId).sendMessage(userType, message);
                }
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

        private void closeEverything() {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
