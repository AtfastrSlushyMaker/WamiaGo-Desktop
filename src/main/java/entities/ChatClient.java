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
            System.out.println("Tentative de connexion au serveur de chat...");
            this.socket = new Socket(ip, port);
            System.out.println("Connexion réussie.");

            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Vérifier si writer est bien initialisé
            if (writer == null) {
                System.err.println("Erreur : writer est null après l'initialisation !");
                return;
            }

            // Convertir user.getRole() en String (si c'est un enum)
            String role = user.getRole().name();
            System.out.println("Envoi de rideId et du rôle de l'utilisateur...");

            writer.write(String.valueOf(rideId));
            writer.newLine();
            writer.write(role);
            writer.newLine();
            writer.flush();

            System.out.println("Informations envoyées avec succès.");
            receiveMessages();
        } catch (IOException e) {
            System.err.println("Erreur lors de la connexion au serveur : " + e.getMessage());
            e.printStackTrace();
            closeEverything();
        }
    }


    public void sendMessage(String message) {
        if (writer == null) {
            System.err.println("Erreur : writer est null, impossible d'envoyer le message.");
            return;
        }

        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
            System.out.println("Message envoyé : " + message);
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
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
            if (reader != null) {
                reader.close();
                System.out.println("Reader fermé.");
            }
            if (writer != null) {
                writer.close();
                System.out.println("Writer fermé.");
            }
            if (socket != null) {
                socket.close();
                System.out.println("Socket fermée.");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
        }
    }


    public interface ChatListener {
        void onMessageReceived(String message);
    }
}
