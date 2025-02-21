package controllers.Announcement;

import entities.Announcement;
import entities.Driver;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import services.AnnouncementService;
import services.DriverService;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AnnouncementController implements Initializable {
    @FXML
    private ListView<Announcement> announcementListView;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btn_workbench1;

    @FXML
    private Button btn_workbench11;

    @FXML
    private Button btnAdd1;

    @FXML
    private Button btnAdd11;

    private Driver currentDriver;
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

            // Charger les annonces
            loadAnnouncements();

            // Gestionnaire d'événements pour le bouton "Ajouter une annonce"
            btnAdd.setOnAction(event -> handleAddButton());

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du conducteur : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Une erreur inattendue s'est produite lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
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
            // Récupérer les annonces pour ce conducteur
            List<Announcement> announcements = announcementService.getAnnouncementsByDriverId(currentDriver.getIdDriver());

            if (announcements != null) {
                announcementListView.getItems().setAll(announcements);
            } else {
                System.err.println("Aucune annonce trouvée pour ce conducteur.");
            }

            // Personnalisation de l'affichage des annonces
            announcementListView.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Announcement announcement, boolean empty) {
                    super.updateItem(announcement, empty);
                    if (empty || announcement == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox vbox = new VBox(5);

                        Label titleLabel = new Label(announcement.getTitle());
                        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                        Label contentLabel = new Label(announcement.getContent());
                        contentLabel.setStyle("-fx-font-size: 14px;");

                        Label dateLabel = new Label(announcement.getDate().toString());
                        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                        HBox actionsBox = new HBox(5);
                        Button editButton = new Button("Modifier");
                        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        editButton.setOnAction(event -> handleEditButtonAction(announcement));

                        Button deleteButton = new Button("Supprimer");
                        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> handleDeleteButton(announcement));

                        actionsBox.getChildren().addAll(editButton, deleteButton);
                        vbox.getChildren().addAll(titleLabel, contentLabel, dateLabel, actionsBox);
                        setGraphic(vbox);
                    }
                }
            });

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du chargement des annonces : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du chargement des annonces : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gère le clic sur le bouton "Modifier".
     *
     * @param announcement L'annonce à modifier
     */
    public void handleEditButtonAction(Announcement announcement) {
        try {
            // Vérifier si l'annonce a un transporteur
            if (announcement.getTransporter() == null) {
                Notifications.create()
                        .title("Erreur")
                        .text("Cette annonce n'a pas de transporteur associé.")
                        .showError();
                return;
            }

            // Vérifier si l'utilisateur connecté est le propriétaire de l'annonce
            if (announcement.getTransporter().getIdDriver() == currentDriver.getIdDriver()) {
                // Créer un Dialog pour l'édition
                Dialog<Announcement> dialog = new Dialog<>();
                dialog.setTitle("Modifier l'annonce");
                dialog.setHeaderText("Modifier les détails de l'annonce");

                // Charger le fichier FXML pour le contenu du Dialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Annoucement/Front/EditAnnouncement.fxml"));
                Parent root = loader.load();

                // Obtenir le contrôleur de la modale
                EditAnnouncementController controller = loader.getController();
                controller.setAnnouncementToEdit(announcement);

                // Ajouter le contenu au Dialog
                dialog.getDialogPane().setContent(root);

                // Ajouter les boutons OK et Annuler
                ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                // Conversion des résultats en objet Announcement
                dialog.setResultConverter(buttonType -> {
                    if (buttonType == saveButtonType) {
                        return announcement;
                    }
                    return null;
                });

                // Affichage du Dialog et gestion de la réponse
                Optional<Announcement> result = dialog.showAndWait();
                result.ifPresent(updatedAnnouncement -> {
                    try {
                        // Mettre à jour l'annonce dans la base de données
                        announcementService.update(updatedAnnouncement);
                        // Recharger la liste des annonces
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
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Vous n'êtes pas autorisé à modifier cette annonce.")
                        .showError();
            }
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
            // Vérifier si l'utilisateur connecté est le propriétaire de l'annonce
            if (announcement.getTransporter().getIdDriver() == currentDriver.getIdDriver()) {
                // Afficher une boîte de dialogue de confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette annonce ?");
                alert.setContentText("Cette action est irréversible.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Supprimer l'annonce de la base de données
                    announcementService.delete(announcement.getIdAnnouncement());
                    // Recharger la liste des annonces
                    loadAnnouncements();
                }
            } else {
                Notifications.create()
                        .title("Erreur")
                        .text("Vous n'êtes pas autorisé à supprimer cette annonce.")
                        .showError();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'annonce : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnAdd1(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/ReservationsTransporter.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAdd1.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnAdd11(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Relocation/Front/RelocationsTransporter.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAdd11.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}