package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import services.AnnouncementService;
import services.DriverService;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AnnouncementController implements Initializable {
    @FXML
    private ListView<Announcement> announcementListView;

    @FXML
    private Button btn_workbench1, btn_workbench11, btn_workbench12, btn_workbench1211;

    @FXML
    private Button btnAdd;



    @FXML
    private Button btnAddReservation;

    @FXML
    private Button btnAddRelocation;

    private Driver currentDriver;
    @FXML
    private GridPane announcementsGrid;

    @FXML
    private TextField keywordTextField; // Champ de texte pour la recherche par mot-clé
    @FXML
    private DatePicker datePicker; // DatePicker pour la recherche par date
    @FXML
    private ComboBox<Announcement.Zone> zoneComboBox; // ComboBox pour la recherche par zone
    @FXML
    private Button searchButton; // Bouton de recherche

    private User loggedInUser = SessionManager.getInstance().getUser(); // Utilisateur connecté

    private final AnnouncementService announcementService = new AnnouncementService();


    @FXML
    public void initialize() {
        btn_workbench1.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        btn_workbench11.setOnAction(event -> loadScene("/rides/rides.fxml"));
    }


    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btn_workbench1.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Récupérer le conducteur actuel en fonction de l'utilisateur connecté
            DriverService driverService = new DriverService();
            currentDriver = driverService.getById(loggedInUser.getId());

            // Peupler le ComboBox avec les zones disponibles
            zoneComboBox.getItems().setAll(Announcement.Zone.values());

            // Charger les annonces initiales
            loadAnnouncements();

            // Ajouter des listeners pour la recherche en temps réel
            keywordTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
            datePicker.valueProperty().addListener((observable, oldValue, newValue) -> handleSearch());
            zoneComboBox.valueProperty().addListener((observable, oldValue, newValue) -> handleSearch());

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du conducteur : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Une erreur inattendue s'est produite lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleHomeButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the dashboard view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRidesButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/rides/rides.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the rides view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBookingsButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/announcements_client.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the bookings view: " + e.getMessage(), Alert.AlertType.ERROR);
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            //showAlert("Error", "Failed to load the login view: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Gère la recherche en temps réel.
     */
    private void handleSearch() {
        String keyword = keywordTextField.getText();
        LocalDate date = datePicker.getValue();
        Announcement.Zone zone = zoneComboBox.getValue();

        try {
            // Passer l'ID du conducteur connecté à la méthode findByFilters
            List<Announcement> announcements = announcementService.findByFilters(keyword, date, zone, currentDriver.getIdDriver());
            announcementsGrid.getChildren().clear();
            displayAnnouncements(announcements);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while searching announcements.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gère le clic sur le bouton "Ajouter une annonce".
     */
    public void handleAddButton() {
        try {
            // Charger la vue d'ajout d'annonce
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/addAnnouncement.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle vue
            Stage stage = (Stage) btnAdd.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge les annonces dans la ListView.
     */


    private void loadAnnouncements() {
        try {
            announcementsGrid.getChildren().clear();
            List<Announcement> announcements = announcementService.getAnnouncementsByDriverId(currentDriver.getIdDriver());

            int column = 0;
            int row = 0;

            for (Announcement announcement : announcements) {
                VBox card = createAnnouncementCard(announcement);
                announcementsGrid.add(card, column, row);

                // Update column and row counters
                column++;
                if (column == 3) { // 3 columns per row
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {

        }
    }

    public void handleSearchButtonAction() {
        String keyword = keywordTextField.getText();
        LocalDate date = datePicker.getValue();
        Announcement.Zone zone = zoneComboBox.getValue();

        try {
            // Passer l'ID du conducteur connecté à la méthode findByFilters
            List<Announcement> announcements = announcementService.findByFilters(keyword, date, zone, currentDriver.getIdDriver());
            announcementsGrid.getChildren().clear();
            displayAnnouncements(announcements);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while searching announcements.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }



    private void displayAnnouncements(List<Announcement> announcements) {
        int column = 0;
        int row = 0;

        for (Announcement announcement : announcements) {
            VBox card = createAnnouncementCard(announcement);
            announcementsGrid.add(card, column, row);

            column++;
            if (column == 3) { // 3 columns per row
                column = 0;
                row++;
            }
        }
    }

    private VBox createAnnouncementCard(Announcement announcement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("grid-card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setSpacing(12);
        card.setStyle("-fx-background-color: #ffffff; "
                + "-fx-border-radius: 8px; "
                + "-fx-border-color: #ddd; "
                + "-fx-padding: 10px 15px; " // Reduced top/bottom padding
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 4);");

        // Title
        Label titleLabel = new Label(announcement.getTitle());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Content with Icon
        ImageView contentIcon = new ImageView(new Image(getClass().getResource("/images/icons/description.png").toExternalForm()));
        contentIcon.setFitWidth(18);
        contentIcon.setFitHeight(18);

        // Truncate content if too long
        String content = announcement.getContent();
        if (content.length() > 100) {
            content = content.substring(0, 100) + "...";
        }

        Label contentLabel = new Label(content);
        contentLabel.getStyleClass().add("card-content");
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(Double.MAX_VALUE);
        contentLabel.setAlignment(Pos.TOP_LEFT);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-text-alignment: left;");

        HBox contentBox = new HBox(8, contentIcon, contentLabel);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // Date Info (Same Row as Content)
        HBox dateBox = createMetaItem(
                new ImageView(new Image(getClass().getResource("/images/icons/date.png").toExternalForm())),
                formatDate(announcement.getDate())
        );

        VBox contentContainer = new VBox(5, contentBox, dateBox);
        contentContainer.setAlignment(Pos.CENTER_LEFT);

        // Zone Information
        HBox zoneBox = createMetaItem(
                new ImageView(new Image(getClass().getResource("/images/icons/place.png").toExternalForm())),
                announcement.getZone().toString()
        );

        // Action Buttons (Edit & Delete) aligned to right
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        ImageView editIcon = createActionIcon("/images/icons/edit.png", event -> handleEditButtonAction(announcement));
        ImageView deleteIcon = createActionIcon("/images/icons/delete.png", event -> handleDeleteButton(announcement));

        // Add Select Button
        Button selectButton = new Button("Select");
        selectButton.setOnAction(event -> openAnnouncementDetails(announcement));
        selectButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        actionBox.getChildren().addAll(editIcon, deleteIcon, selectButton);

        // Make zoneBox expand and push actionBox to the right
        HBox metaRow = new HBox(10, zoneBox, actionBox);
        HBox.setHgrow(zoneBox, Priority.ALWAYS);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        // Final Assembly
        VBox mainContent = new VBox(5, contentContainer, metaRow);
        card.getChildren().addAll(titleLabel, mainContent);

        return card;
    }

    private void openAnnouncementDetails(Announcement announcement) {
        Stage modalStage = new Stage();
        modalStage.setTitle("Announcement Details");
        modalStage.initModality(Modality.APPLICATION_MODAL);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        VBox modalLayout = new VBox(15);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setAlignment(Pos.CENTER_LEFT);
        modalLayout.setStyle("-fx-background-color: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 4);");

        // Titre de l'annonce
        Label title = new Label(announcement.getTitle());
        title.setStyle("-fx-font-size: 18px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #5A6BE5; " // Couleur bleue
                + "-fx-alignment: center;"); // Centrer le texte dans le Label
        title.setMaxWidth(Double.MAX_VALUE); // Prendre toute la largeur disponible
        VBox.setMargin(title, new Insets(0, 0, 10, 0)); // Marge en bas pour l'espacement

        // Icônes et détails
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView contentIcon = createIcon("/images/icons/description.png");
        ImageView zoneIcon = createIcon("/images/icons/place.png");

        // Date
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + formatDate(announcement.getDate()));

        // Contenu complet (sans troncature)
        Label contentLabel = new Label(announcement.getContent());
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-wrap-text: true;");
        contentLabel.setMaxWidth(500); // Limiter la largeur pour un meilleur affichage

        HBox contentBox = createLabeledIconBox(contentIcon, "Content: ");
        contentBox.getChildren().add(contentLabel); // Ajouter le contenu complet

        // Zone
        HBox zoneBox = createLabeledIconBox(zoneIcon, "Zone: " + announcement.getZone().toString());

        // Bouton "Close"
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; "
                + "-fx-text-fill: white; "
                + "-fx-font-weight: bold; "
                + "-fx-background-radius: 5px; "
                + "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        // Ajouter une image statique avant le bouton "Close"
        ImageView headerImage = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/truck.png")));
        headerImage.setFitWidth(300); // Ajuster la largeur de l'image
        headerImage.setPreserveRatio(true);
        headerImage.setSmooth(true);
        headerImage.setCache(true);

        // Ajouter tous les éléments au layout
        modalLayout.getChildren().addAll(title, dateBox, contentBox, zoneBox, headerImage, closeButtonContainer);

        // Ajouter un ScrollPane pour permettre le défilement si le contenu est trop long
        ScrollPane scrollPane = new ScrollPane(modalLayout);
        scrollPane.setFitToWidth(true); // Adapter la largeur au contenu
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        stackPane.getChildren().add(scrollPane);

        // Ajuster la taille du dialogue
        Scene modalScene = new Scene(stackPane, 500, 500); // Taille agrandie pour accommoder l'image
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    // Helper method to create an HBox with an icon and a label
    private HBox createLabeledIconBox(ImageView icon, String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

        HBox box = new HBox(10); // Spacing between icon and label
        box.getChildren().addAll(icon, label);
        box.setAlignment(Pos.CENTER_LEFT); // Align to the left
        return box;
    }

    // Helper method to create icons
    private ImageView createIcon(String iconPath) {
        Image image = new Image(getClass().getResourceAsStream(iconPath));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20); // Set a consistent height
        imageView.setFitWidth(20); // Set a consistent width
        return imageView;
    }

    private HBox createMetaItem(ImageView icon, String text) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);

        icon.setFitWidth(16);
        icon.setFitHeight(16);

        Label label = new Label(text);
        label.getStyleClass().add("meta-item");
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        box.getChildren().addAll(icon, label);
        return box;
    }

    private ImageView createActionIcon(String imagePath, EventHandler<MouseEvent> eventHandler) {
        ImageView icon = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
        icon.setFitWidth(22);
        icon.setFitHeight(22);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(eventHandler);
        icon.setStyle("-fx-opacity: 0.7;");

        // Hover effect
        icon.setOnMouseEntered(e -> icon.setStyle("-fx-opacity: 1;"));
        icon.setOnMouseExited(e -> icon.setStyle("-fx-opacity: 0.7;"));

        return icon;
    }

    private String formatDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        return sdf.format(new Date(timestamp.getTime()));
    }

    /**
     * Gère le clic sur le bouton "Modifier".
     *
     * @param announcement L'annonce à modifier
     */
    public void handleEditButtonAction(Announcement announcement) {
        try {
            Dialog<Announcement> dialog = new Dialog<>();
            dialog.setTitle("Modifier l'annonce");
            dialog.setHeaderText("Modifier les détails de l'annonce");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/EditAnnouncement.fxml"));
            Parent root = loader.load();

            EditAnnouncementController controller = loader.getController();
            controller.setAnnouncementToEdit(announcement);

            dialog.getDialogPane().setContent(root);

            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == saveButtonType) {
                    return announcement;
                }
                return null;
            });

            Optional<Announcement> result = dialog.showAndWait();
            result.ifPresent(updatedAnnouncement -> {
                try {
                    announcementService.update(updatedAnnouncement);
                    // Rafraîchir la liste des annonces après la mise à jour
                    loadAnnouncements();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Une erreur s'est produite lors de la mise à jour de l'annonce.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'interface d'édition : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère le clic sur le bouton "Supprimer".
     *
     * @param announcement L'annonce à supprimer
     */
    public void handleDeleteButton(Announcement announcement) {
        try {
            // Custom Delete Confirmation Dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Confirm Deletion");
            dialog.setHeaderText(null); // Remove default header for a cleaner look

            // Apply CSS Styles
            dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

            // Icon
            ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/images/icons/delete.png").toExternalForm()));
            deleteIcon.setFitWidth(50);
            deleteIcon.setFitHeight(50);

            // Title Label
            Label titleLabel = new Label("Are you sure you want to delete this announcement?");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;"); // Red for danger

            // Message Label
            Label messageLabel = new Label("This action cannot be undone.");
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            // Layout
            VBox layout = new VBox(15);
            layout.setAlignment(Pos.CENTER);
            layout.getChildren().addAll(deleteIcon, titleLabel, messageLabel);

            dialog.getDialogPane().setContent(layout);

            // Buttons
            ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(deleteButton, cancelButton);

            // Show Dialog & Handle Result
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == deleteButton) {
                announcementService.delete(announcement.getIdAnnouncement());
                loadAnnouncements();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting announcement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnAddReservation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/ReservationsTransporter.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAddReservation.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnAddRelocation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Relocation/Front/RelocationsTransporter.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAddRelocation.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    private void refreshAnnouncements() {
//        announcementsGrid.getChildren().clear();
//        loadAnnouncements();
//    }
}
