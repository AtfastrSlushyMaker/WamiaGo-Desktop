package controllers.request;

import entities.Request;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.RequestService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RequestController {

    @FXML
    private VBox requestsContainer; // Conteneur des cartes (VBox dans le FXML)

    @FXML
    private Button home_button;

    private final RequestService requestService = new RequestService(); // Service pour accéder aux données de la base

    private static final int CARDS_PER_ROW = 2; // Nombre de cartes par ligne

    @FXML
    void initialize() {
        try {
            // Récupérer toutes les demandes avec la fonction read()
            List<Request> requests = requestService.read();

            // Créer un HBox pour chaque ligne de cartes
            HBox row = new HBox(20); // Conteneur pour les cartes sur une ligne
            int count = 0;

            // Remplir les cartes avec les demandes récupérées
            for (Request request : requests) {
                // Créer une nouvelle carte (un VBox)
                VBox card = new VBox(10);

                // Créer les labels pour afficher les informations de la demande
                Label clientNameLabel = new Label(request.getClient().getName());
                Label locationsLabel = new Label("From: " + request.getDepartureLocation().getAddress() +
                        " To: " + request.getArrivalLocation().getAddress());
                Label statusLabel = new Label("Status: " + request.getStatus().toString());
                Label dateLabel = new Label("Date: " + request.getRequestDate().toLocalDate());

                // Ajouter les labels à la carte (VBox)
                card.getChildren().addAll(clientNameLabel, locationsLabel, statusLabel, dateLabel);

                // Créer les boutons de suppression et de mise à jour
                Button deleteButton = new Button("Delete");
                Button updateButton = new Button("Update");

                // Ajouter les boutons à la carte
                card.getChildren().addAll(deleteButton, updateButton);

                // Ajouter la carte à la ligne
                row.getChildren().add(card);
                count++;

                // Ajouter une nouvelle ligne après chaque 2 cartes
                if (count % CARDS_PER_ROW == 0) {
                    requestsContainer.getChildren().add(row);
                    row = new HBox(20);
                }
            }

            // Ajouter la dernière ligne si elle contient encore des cartes
            if (!row.getChildren().isEmpty()) {
                requestsContainer.getChildren().add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Retour à l'écran d'accueil
    @FXML
    void goToHome() {
        // Implémentation pour le bouton de retour
        System.out.println("Naviguer vers l'écran d'accueil");
    }
}
