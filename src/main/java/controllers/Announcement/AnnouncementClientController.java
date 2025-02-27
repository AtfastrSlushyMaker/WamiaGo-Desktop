package controllers.Announcement;

import entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.*;
import utils.SessionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//import static sun.security.x509.OIDMap.getClass;


public class AnnouncementClientController {
    public Button btn_workbench1;
    @FXML
    private ListView<Announcement> announcementListView;

    private final AnnouncementService announcementService = new AnnouncementService();
    private final ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService();
    private final UserService userService = new UserService();
    @FXML
    private Button btnclient;

    @FXML
    private Button btnAddRelocationClient;

    private User loggedInUser; // Utilisateur connecté

    @FXML
    public void initialize() {
        // Récupérer l'utilisateur connecté
        loggedInUser = SessionManager.getInstance().getUser();


        announcementListView.getStylesheets().add(getClass().getResource("/Annoucement/Front/announcement.css").toExternalForm());
        loadAnnouncements();

        btn_workbench1.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dashboard.fxml"));
                Parent homeRoot = loader.load();
                Scene homeScene = new Scene(homeRoot);
                Stage stage = (Stage) btn_workbench1.getScene().getWindow();
                stage.setScene(homeScene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void loadAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.read();
            announcementListView.getItems().setAll(announcements);

            announcementListView.setCellFactory(new Callback<>() {
                @Override
                public ListCell<Announcement> call(ListView<Announcement> listView) {
                    return new ListCell<>() {
                        @Override
                        protected void updateItem(Announcement announcement, boolean empty) {
                            super.updateItem(announcement, empty);
                            if (empty || announcement == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                VBox vbox = new VBox(10);
                                vbox.setPadding(new Insets(15));
                                vbox.setAlignment(Pos.CENTER); // Center all content vertically and horizontally
                                vbox.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 10; -fx-background-radius: 10;");
                                vbox.getStyleClass().add("announcement-card");

                                // Title Label
                                Label titleLabel = new Label(announcement.getTitle());
                                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");

                                // Content with Icon
                                ImageView contentIcon = new ImageView(new Image(getClass().getResource("/images/icons/description.png").toExternalForm()));
                                contentIcon.setFitWidth(18);
                                contentIcon.setFitHeight(18);

                                Label contentLabel = new Label(announcement.getContent());
                                contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");
                                contentLabel.setWrapText(true);

                                HBox contentBox = new HBox(8, contentIcon, contentLabel);
                                contentBox.setAlignment(Pos.CENTER); // Center content horizontally

                                // Date Info
                                ImageView dateIcon = new ImageView(new Image(getClass().getResource("/images/icons/date.png").toExternalForm()));
                                dateIcon.setFitWidth(16);
                                dateIcon.setFitHeight(16);

                                Label dateLabel = new Label("Date: " + announcement.getDate().toString());
                                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");

                                HBox dateBox = new HBox(8, dateIcon, dateLabel);
                                dateBox.setAlignment(Pos.CENTER); // Center date horizontally

                                // Zone Information
                                ImageView zoneIcon = new ImageView(new Image(getClass().getResource("/images/icons/place.png").toExternalForm()));
                                zoneIcon.setFitWidth(16);
                                zoneIcon.setFitHeight(16);

                                Label zoneLabel = new Label("Zone: " + announcement.getZone().toString());
                                zoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");

                                HBox zoneBox = new HBox(8, zoneIcon, zoneLabel);
                                zoneBox.setAlignment(Pos.CENTER); // Center zone horizontally

                                // Buttons
                                Button selectButton = new Button("Détails");
                                selectButton.getStyleClass().add("select-button");
                                selectButton.setOnAction(event -> openAnnouncementDetails(announcement));
                                styleButton(selectButton);

                                Button reserveButton = new Button("Reserve");
                                reserveButton.getStyleClass().add("reserve-button");
                                reserveButton.setOnAction(event -> handleReserveButtonAction(announcement));
                                styleButton(reserveButton);

                                // Button Box
                                HBox buttonBox = new HBox(10, selectButton, reserveButton);
                                buttonBox.setAlignment(Pos.CENTER); // Center buttons horizontally

                                // Add children to VBox
                                vbox.getChildren().addAll(titleLabel, contentBox, dateBox, zoneBox, buttonBox);
                                setGraphic(vbox);
                            }
                        }
                    };
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to style buttons
    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 10px 15px; -fx-font-size: 14px; -fx-border-radius: 5; -fx-background-radius: 5;"));
    }


    private void openAnnouncementDetails(Announcement announcement) {
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

        Label titleLabel = new Label(announcement.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Load icons
        ImageView dateIcon = createIcon("/images/icons/date.png");
        ImageView zoneIcon = createIcon("/images/icons/place.png"); // Assuming you have an icon for the zone
        ImageView contentIcon = createIcon("/images/icons/description.png");

        // Labels with icons
        HBox dateBox = createLabeledIconBox(dateIcon, "Date: " + announcement.getDate());
        HBox zoneBox = createLabeledIconBox(zoneIcon, "Zone: " + announcement.getZone());
        HBox contentBox = createLabeledIconBox(contentIcon, "Content: " + announcement.getContent());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.setStyle("-fx-background-color: #000000; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px; " +
                "-fx-padding: 8px 16px;");

        HBox closeButtonContainer = new HBox(closeButton);
        closeButtonContainer.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(titleLabel, dateBox, zoneBox, contentBox, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    // Helper method to create labeled icon boxes
    private HBox createLabeledIconBox(ImageView icon, String label) {
        Label textLabel = new Label(label);
        textLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        HBox box = new HBox(10, icon, textLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // Helper method to load icons
    private ImageView createIcon(String path) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
        icon.setFitHeight(16);
        icon.setFitWidth(16);
        return icon;
    }

    private void handleReserveButtonAction(Announcement announcement) {
        // Create Custom Dialog
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle("Reserve Announcement");
        dialog.setHeaderText(null); // Cleaner UI without default header
        dialog.getDialogPane().setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Load CSS for better design
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialog.css").toExternalForm());

        // Icon
        ImageView reserveIcon = new ImageView(new Image(getClass().getResource("/images/icons/date.png").toExternalForm()));
        reserveIcon.setFitWidth(50);
        reserveIcon.setFitHeight(50);

        // Title Label
        Label titleLabel = new Label("Fill in the Reservation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        // Form Fields
        DatePicker datePicker = new DatePicker();
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Enter description...");

        ComboBox<Station> startLocationComboBox = new ComboBox<>();
        ComboBox<Station> endLocationComboBox = new ComboBox<>();

        // CellFactory for displaying station addresses
        Callback<ListView<Station>, ListCell<Station>> cellFactory = new Callback<>() {
            @Override
            public ListCell<Station> call(ListView<Station> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Station station, boolean empty) {
                        super.updateItem(station, empty);
                        if (empty || station == null) {
                            setText(null);
                        } else {
                            setText(station.getLocation().getAddress());  // Show only address
                        }
                    }
                };
            }
        };

        // Apply CellFactory to ComboBoxes
        startLocationComboBox.setCellFactory(cellFactory);
        startLocationComboBox.setButtonCell(cellFactory.call(null));
        endLocationComboBox.setCellFactory(cellFactory);
        endLocationComboBox.setButtonCell(cellFactory.call(null));

        // Load stations
        try {
            startLocationComboBox.getItems().addAll(stationService.read());
            endLocationComboBox.getItems().addAll(stationService.read());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Start Location:"), 0, 2);
        grid.add(startLocationComboBox, 1, 2);
        grid.add(new Label("End Location:"), 0, 3);
        grid.add(endLocationComboBox, 1, 3);

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(reserveIcon, titleLabel, grid);
        dialog.getDialogPane().setContent(layout);

        // Buttons
        ButtonType reserveButtonType = new ButtonType("Reserve", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);

        // Result conversion to Reservation object
        dialog.setResultConverter(buttonType -> {
            if (buttonType == reserveButtonType) {
                if (datePicker.getValue() == null || descriptionField.getText().trim().isEmpty() ||
                        startLocationComboBox.getValue() == null || endLocationComboBox.getValue() == null) {
                    showErrorDialog("Invalid Input", "Please fill in all fields.");
                    return null;
                }

                // Create new reservation
                Reservation reservation = new Reservation();
                reservation.setDate(Timestamp.valueOf(datePicker.getValue().atStartOfDay()));
                reservation.setDescription(descriptionField.getText());
                reservation.setStartLocation(startLocationComboBox.getValue().getLocation());
                reservation.setEndLocation(endLocationComboBox.getValue().getLocation());
                reservation.setAnnouncement(announcement);
                reservation.setUser(loggedInUser); // Associate logged-in user

                return reservation;
            }
            return null;
        });

        // Show Dialog & Handle Response
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(reservation -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Reservation");
            confirmAlert.setHeaderText("Are you sure you want to reserve this announcement?");
            confirmAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                try {
                    reservationService.create(reservation);
                    refreshAnnouncements();
                } catch (SQLException e) {
                    showErrorDialog("Error Reserving Announcement", e.getMessage());
                }
            }
        });
    }

    /**
     * Show an error dialog with a custom message.
     */
    private void showErrorDialog(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }


    private void refreshAnnouncements() {
        announcementListView.getItems().clear();
        loadAnnouncements();
    }

    public void btnclient(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation/Front/Reservationsclient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnclient.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnAddRelocationClient(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Relocation/Front/RelocationClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAddRelocationClient.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}