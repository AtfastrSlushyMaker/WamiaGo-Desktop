package controllers.Relocation;

import controllers.Chat_Relocation.ChatController;
import entities.Driver;
import entities.Relocation;
import entities.User;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.RelocationService;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RelocationClentController {
    @FXML
    private Button bookings_button, history_button, home_button, logout_button, rides_button;
    @FXML
    private HBox root;
    @FXML
    private FlowPane stationFlowPane;

    private final RelocationService relocationService = new RelocationService();
    private String emailtoUser;

    private Driver currentDriver;
    private User loggedInUser = SessionManager.getInstance().getUser();

    @FXML

    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Relocation/front/relocation.css").toExternalForm());
        setupNavigation();

        // Charger les relocalisations du client connecté
        loadStationsIntoFlowPane();
    }
    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
    }

    @FXML
    public void handleHomeButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the dashboard view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRidesButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rides/rides.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) rides_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the rides view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBookingsButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements_client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) bookings_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the bookings view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogoutButtonAction(ActionEvent event) {
        try {
            // Nettoyer la session
            SessionManager.getInstance().logout();

            // Naviguer vers la vue de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.front/loginSignup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logout_button.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load the login view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStationsIntoFlowPane() {
        try {
            // Récupérer l'utilisateur connecté
            User loggedInUser = SessionManager.getInstance().getUser();

            // Récupérer les relocalisations du client connecté
            List<Relocation> relocations = relocationService.getRelocationsByClientId(loggedInUser.getId());

            // Ajouter chaque relocalisation à l'interface utilisateur
            for (Relocation station : relocations) {
                VBox stationCard = createStationCard(station);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Relocation station) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(15));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);
        stationCard.setSpacing(8);

        // Image and title
        HBox imageAndTextBox = createImageAndTextBox(station);

        // Labels for details
        Label reservationLabel = new Label("Reservation: " + station.getReservation().getDescription());
        Label dateLabel = new Label("Date: " + station.getDate());
        Label costLabel = new Label("Cost: " + station.getCost());
        Label statusLabel = new Label("Status: " + (station.isStatus() ? "Completed" : "Pending"));

        // Appliquer la couleur en fonction du statut
        if (station.isStatus()) {
            statusLabel.getStyleClass().add("status-label COMPLETED");
        } else {
            statusLabel.getStyleClass().add("status-label ON_GOING");
        }

        // Button with icon for selecting
        Button selectButton = createIconButton("/images/icons/eye.png", event -> openStationDetails(station));
        Button messengerButton = createIconButton("/images/icons/messenger.png", event -> openMessenger(station));

        // Button container: Select and Messenger buttons
        HBox buttonBox = new HBox(10, selectButton, messengerButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // Add elements to card
        stationCard.getChildren().addAll(imageAndTextBox, reservationLabel, dateLabel, costLabel, statusLabel, buttonBox);

        // Optional: Add hover effects
        stationCard.setOnMouseEntered(event -> {
            stationCard.setScaleX(1.05);
            stationCard.setScaleY(1.05);
        });

        stationCard.setOnMouseExited(event -> {
            stationCard.setScaleX(1);
            stationCard.setScaleY(1);
        });

        return stationCard;
    }

    // Méthode pour gérer l'ouverture de la messagerie
    private void openMessenger(Relocation station) {
        // Exécuter la récupération de l'e-mail en arrière-plan pour ne pas bloquer le thread JavaFX
        Task<String> fetchEmailTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return relocationService.getEmailDriverByRelocation(station);
            }
        };

        // Gérer la réussite de la tâche
        fetchEmailTask.setOnSucceeded(event -> {
            try {
                emailtoUser = fetchEmailTask.get(); // Récupérer l'e-mail de l'utilisateur

                // Charger l'interface de chat
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chat_Relocation/chat.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur de l'interface de chat
                ChatController chatController = loader.getController();

                // Passer les e-mails au contrôleur
                chatController.setToEmail(emailtoUser);


                // E-mail de l'utilisateur destinataire
                chatController.setFromEmail(loggedInUser.getEmail());
                chatController.refreshChatList();
                chatController.initialize();
                // Afficher la fenêtre de chat
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Chat Relocation");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Erreur de chargement", "Impossible de charger l'interface de chat.");
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // Gérer les échecs de la tâche
        fetchEmailTask.setOnFailed(event -> {
            Throwable exception = fetchEmailTask.getException();
            exception.printStackTrace();
            showErrorDialog("Erreur de communication", "Impossible de récupérer l'e-mail de l'utilisateur.");
        });

        // Démarrer la tâche dans un nouveau thread
        new Thread(fetchEmailTask).start();
    }

    private Button createIconButton(String imagePath, EventHandler<ActionEvent> eventHandler) {
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(20); // Adjust icon size
        icon.setFitHeight(20);

        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
        button.setOnAction(eventHandler);
        return button;
    }

    private HBox createImageAndTextBox(Relocation station) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/relocation.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);

        Text nameText = new Text(station.getReservation().getDescription());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #2e2e2e;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }


    private void openStationDetails(Relocation station) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Détails");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);");

        Label title = new Label(station.getReservation().getDescription());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView statusIcon = createIcon(station.isStatus() ? "/images/icons/check.png" : "/images/icons/pending.png");
        ImageView costIcon = createIcon("/images/icons/money.png");

        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + station.getDate());
        HBox statusBox = createLabeledIconBox(statusIcon, "Status: " + (station.isStatus() ? "Completed" : "Pending"));
        HBox costBox = createLabeledIconBox(costIcon, "Cost: " + station.getCost());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(title, dateBox, statusBox, costBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 280);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    /**
     * Creates an ImageView with a specific icon size.
     */
    private ImageView createIcon(String imagePath) {
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        return icon;
    }


    /**
     * Creates an HBox containing an icon and a label for a consistent layout.
     */
    private HBox createLabeledIconBox(ImageView icon, String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;"); // Darker text for contrast

        HBox box = new HBox(8, icon, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @FXML
    public void handleBackButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements_client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the announcements view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }

    private void refreshRelocations() {
        stationFlowPane.getChildren().clear();
        loadStationsIntoFlowPane();
    }
}