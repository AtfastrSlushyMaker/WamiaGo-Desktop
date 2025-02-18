package controllers.request;
import entities.Request;
import services.RequestService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import java.sql.SQLException;
import java.util.List;

public class RequestController {
    @FXML
    private VBox requestsContainer; // Conteneur des HBox (lignes de cards)

    private final RequestService requestService = new RequestService(); // Service DB

    private static final int CARDS_PER_ROW = 2; // Nombre de cards par ligne

    @FXML
    public void initialize() {
        loadRequests(); // Charger les requests au démarrage
    }

    private void loadRequests() {
        try {
            List<Request> requests = requestService.read(); // Récupérer les requests depuis la DB
            requestsContainer.getChildren().clear(); // Nettoyer avant d'ajouter les nouvelles

            HBox currentRow = null;

            for (int i = 0; i < requests.size(); i++) {
                if (i % CARDS_PER_ROW == 0) {
                    currentRow = new HBox();
                    currentRow.setSpacing(15);
                    requestsContainer.getChildren().add(currentRow);
                }
                if (currentRow != null) {
                    currentRow.getChildren().add(createRequestCard(requests.get(i)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createRequestCard(Request request) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setStyle("-fx-padding: 10px; -fx-border-color: #ddd; -fx-background-radius: 10; -fx-border-radius: 10; -fx-background-color: #fff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Label clientLabel = new Label("Client: " + request.getClient().getName());
        Label statusLabel = new Label("Status: " + request.getStatus().toString());
        Label dateLabel = new Label("Date: " + request.getRequestDate().toString());

        HBox buttonBox = new HBox();
        Button deleteButton = new Button("❌ Supprimer");
        Button updateButton = new Button("✏️ Modifier");

        // Action du bouton Supprimer
        deleteButton.setOnAction(event -> {
            try {
                requestService.delete(request.getIdRequest());
                loadRequests(); // Rafraîchir la liste après suppression
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(updateButton, deleteButton);
        buttonBox.setSpacing(10);

        card.getChildren().addAll(clientLabel, statusLabel, dateLabel, buttonBox);
        return card;
    }
}
