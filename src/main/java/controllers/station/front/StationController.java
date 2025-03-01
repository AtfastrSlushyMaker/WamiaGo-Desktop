package controllers.station.front;

import entities.Bicycle;
import entities.BicycleRental;
import entities.Station;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import services.BicycleRentalService;
import services.BicycleService;
import services.StationService;
import utils.SessionManager;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StationController {
    private final StationService stationService = new StationService();
    private final List<Stage> openModals = new ArrayList<>();
    @FXML
    private Button bookings_button;
    @FXML
    private Button history_button;
    @FXML
    private Button home_button;
    @FXML
    private Button logout_button;
    @FXML
    private StackPane stackPaneMap;
    @FXML
    private Pane pane_1121;
    @FXML
    private Button rides_button;
    @FXML
    private HBox root;
    @FXML
    private AnchorPane side_ankerpane;
    @FXML
    private FlowPane stationFlowPane;
    @FXML
    private Label bikeCount;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button my_bikes_button;
    @FXML
    private WebView map;
    @FXML
    private Button sortButton;
    @FXML
    private TextField searchField;
    private WebEngine webEngine;
    private Timeline reservationTimeline;
    @FXML
    private ProgressIndicator loadingSpinner;

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/station/front/station.css").toExternalForm());
        // Add a loading spinner to the map
        loadingSpinner = new ProgressIndicator();
        loadingSpinner.setVisible(false);
        stackPaneMap.getChildren().add(loadingSpinner);
        stackPaneMap.setStyle(
                "-fx-background-color: transparent; " + // Transparent background
                        "-fx-border-color: #cccccc; " + // Light gray border
                        "-fx-border-width: 1px; " + // Thin border
                        "-fx-border-radius: 10px; " + // Rounded corners
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);" // Subtle shadow
        );
        webEngine = map.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.RUNNING) {
                loadingSpinner.setVisible(true);
            } else if (newState == Worker.State.SUCCEEDED) {
                loadingSpinner.setVisible(false);
            }
        });
        loadStationsIntoFlowPane();
        setupNavigation();
        loadMap();
        searchField.setOnKeyReleased(event -> search());
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private void loadMap() {
        webEngine = map.getEngine();
        map.prefWidthProperty().bind(stackPaneMap.widthProperty());
        map.prefHeightProperty().bind(stackPaneMap.heightProperty());
        String path = getClass().getResource("/maps/map.html").toExternalForm();
        webEngine.load(path);

        // Listen for map load completion
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                // Expose the Java method to JavaScript
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("JavaBridge", this);

                // Add markers to the map using data from the database
                try {
                    addMarkersToMap(stationService.read());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // JavaScript bridge to handle marker clicks
    public void handleMarkerClick(String stationName) {
        Platform.runLater(() -> {
            System.out.println("Marker clicked: " + stationName);
            searchField.setText(stationName);
            search();
        });
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
        my_bikes_button.setOnAction(event -> loadScene("/station/front/myBikes/myBikes.fxml"));

    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStationsIntoFlowPane() {
        ProgressIndicator loadingSpinner = new ProgressIndicator();

        loadingSpinner.getStyleClass().add("loading-spinner");
        stationFlowPane.getChildren().add(loadingSpinner);


        new Thread(() -> {
            try {
                List<Station> stations = stationService.getSortedStationsByUserDistance(SessionManager.getInstance().getUser());
                Platform.runLater(() -> {
                    stationFlowPane.getChildren().clear();
                    for (Station station : stations) {
                        VBox stationCard = createStationCard(station);
                        stationFlowPane.getChildren().add(stationCard);
                    }
                });
            } catch (SQLException e) {
                Platform.runLater(() -> showErrorDialog("Database Error", "Failed to load stations."));
                e.printStackTrace();
            }
        }).start();
    }

    private VBox createStationCard(Station station) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(10));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);

        HBox imageAndTextBox = createImageAndTextBox(station);
        bikeCount = new Label("Bikes Available: " + stationService.getAvailableBikes(station).size());
        bikeCount.getStyleClass().add("station-bike-count");

        Button selectButton = createSelectButton(station);

        stationCard.getChildren().addAll(imageAndTextBox, bikeCount, selectButton);

        stationCard.setOnMouseExited(event -> {
            stationCard.setScaleX(1);
            stationCard.setScaleY(1);
        });

        stationCard.setOnMouseEntered(event -> {
            stationCard.setScaleX(1.05);
            stationCard.setScaleY(1.05);
        });

        if (station.getStatus() == Station.STATUS.disabled || new StationService().getAvailableBikes(station).size() == 0 || station.getAvailable_docks() == 0) {
            stationCard.setDisable(true);
            stationCard.setStyle("-fx-opacity: 0.5;");
        }
        return stationCard;
    }

    private HBox createImageAndTextBox(Station station) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setFillHeight(true);

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/station/icons/bicycle_station_white.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);
        stationImage.setPreserveRatio(true);

        Text nameText = new Text(station.getName());
        nameText.setWrappingWidth(180);
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");

        HBox.setHgrow(nameText, Priority.ALWAYS);
        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

    private Button createSelectButton(Station station) {
        Button selectButton = new Button("Select");
        selectButton.getStyleClass().add("station-button");
        selectButton.setOnAction(e -> openStationDetails(station));
        return selectButton;
    }

    private void openStationDetails(Station station) {
        Stage modalStage = new Stage();
        modalStage.initStyle(StageStyle.TRANSPARENT); // Keep transparent background
        modalStage.setTitle("Available Bicycles at " + station.getName());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0001); -fx-padding: 50px;");

        VBox modalLayout = new VBox(20); // Increased spacing
        modalLayout.setPadding(new Insets(40)); // Increased padding
        modalLayout.setStyle(
                "-fx-background-color: #2c2c2c; " +
                        "-fx-background-radius: 20px;" // Rounded corners
        );

        // Title
        Label titleLabel = new Label("🚲 Available Bicycles at " + station.getName());
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setWrapText(true); // Enable text wrapping
        titleLabel.setMaxWidth(600); // Set a maximum width for the title (larger to fit more text)

        // Ensure the title label is centered and wraps properly
        HBox titleContainer = new HBox(titleLabel);
        titleContainer.setAlignment(Pos.CENTER); // Center the title
        titleContainer.setMaxWidth(600); // Constrain the width of the container

        FlowPane bicycleFlowPane = new FlowPane();
        bicycleFlowPane.setHgap(15); // Horizontal gap between bike cards
        bicycleFlowPane.setVgap(15); // Vertical gap between bike cards
        bicycleFlowPane.setStyle("-fx-padding: 10px;");

        addAvailableBicycles(bicycleFlowPane, station);

        // Close Button Styling
        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff4444; " + // Red background
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14px 24px; " +
                        "-fx-min-width: 160px; " +
                        "-fx-background-radius: 14px; " +
                        "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> modalStage.close());

        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);
        closeButtonContainer.getChildren().add(closeButton);

        // Add a scroll pane for the bicycle flow pane
        ScrollPane scrollPane = new ScrollPane(bicycleFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Hide horizontal scrollbar
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scrollbar only if needed

        // Set the background of the ScrollPane to transparent
        scrollPane.setStyle(
                "-fx-background-color: transparent; " + // Transparent background
                        "-fx-background-insets: 0; " +
                        "-fx-padding: 0;"
        );

        // Apply CSS to style the ScrollPane and its internal components
        URL cssResource = getClass().getResource("/station/front/dark-scrollpane.css");
        if (cssResource == null) {
            System.err.println("CSS file not found! Check the path: /station/front/dark-scrollpane.css");
        } else {
            scrollPane.getStylesheets().add(cssResource.toExternalForm());
        }

        // Add the title container instead of the title label directly
        modalLayout.getChildren().addAll(titleContainer, scrollPane, closeButtonContainer);
        stackPane.getChildren().add(modalLayout);

        // Increase the modal size
        Scene modalScene = new Scene(stackPane, 800, 600); // Larger window size
        modalScene.setFill(Color.TRANSPARENT); // Keep scene transparent

        // Make the window draggable
        final double[] dragDelta = new double[2];
        modalStage.setOpacity(1.0);
        modalStage.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            dragDelta[0] = mouseEvent.getScreenX() - modalStage.getX();
            dragDelta[1] = mouseEvent.getScreenY() - modalStage.getY();
        });
        modalStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            modalStage.setX(mouseEvent.getScreenX() - dragDelta[0]);
            modalStage.setY(mouseEvent.getScreenY() - dragDelta[1]);
        });

        modalStage.setScene(modalScene);
        modalStage.show();
        openModals.add(modalStage);
    }


    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addAvailableBicycles(FlowPane bicycleFlowPane, Station station) {
        for (Bicycle bicycle : stationService.getAvailableBikes(station)) {
            // Create a VBox to hold the bike card
            VBox bikeCard = new VBox(10); // Spacing between elements
            bikeCard.setPadding(new Insets(15)); // Padding inside the card
            bikeCard.setAlignment(Pos.CENTER); // Center align content
            bikeCard.setStyle(
                    "-fx-background-color: #3a3a3a; " + // Dark grey background
                            "-fx-background-radius: 10px; " + // Rounded corners
                            "-fx-border-radius: 10px; " + // Rounded corners for border
                            "-fx-border-color: #555555; " + // Light grey border
                            "-fx-border-width: 1px; " + // Thin border
                            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);" // Subtle shadow
            );

            // Add hover effect
            bikeCard.setOnMouseEntered(event -> {
                bikeCard.setStyle(
                        "-fx-background-color: #444444; " + // Slightly lighter on hover
                                "-fx-background-radius: 10px; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-border-color: #666666; " + // Lighten border on hover
                                "-fx-border-width: 1px; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 7, 0, 0, 3);" // Slightly stronger shadow on hover
                );
            });

            bikeCard.setOnMouseExited(event -> {
                bikeCard.setStyle(
                        "-fx-background-color: #3a3a3a; " +
                                "-fx-background-radius: 10px; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-border-color: #555555; " +
                                "-fx-border-width: 1px; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);"
                );
            });

            // Bike icon
            Image bikeIcon = new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm());
            ImageView bikeIconView = new ImageView(bikeIcon);
            bikeIconView.setFitHeight(50); // Slightly larger icon
            bikeIconView.setFitWidth(50);
            bikeIconView.setPreserveRatio(true);

            // Bike details
            Label bikeLabel = new Label("Bike");
            bikeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

            Label bikeStatusLabel = new Label("Status: " + bicycle.getStatus());
            bikeStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #bbbbbb;");

            // Add elements to the bike card
            bikeCard.getChildren().addAll(bikeIconView, bikeLabel, bikeStatusLabel);

            // Add click action to show bike details
            bikeCard.setOnMouseClicked(event -> showBikeDetails(bicycle, station));

            // Add the bike card to the flow pane
            bicycleFlowPane.getChildren().add(bikeCard);
        }
    }

    private void showBikeDetails(Bicycle bicycle, Station station) {
        Stage modalStage = new Stage();
        modalStage.initStyle(StageStyle.TRANSPARENT); // Keep transparent background
        modalStage.setTitle("Bike Details: at " + station.getName());

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0001); -fx-padding: 50px;");

        HBox modalLayout = new HBox(40); // Increased spacing
        modalLayout.setPadding(new Insets(40)); // Increased padding
        modalLayout.setStyle(
                "-fx-background-color: #2c2c2c; " +
                        "-fx-background-radius: 20px;" // Removed shadow effect
        );

        VBox textLayout = new VBox(20); // Increased spacing
        textLayout.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🚲 Bike Details");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label batteryLabel = new Label("🔋 Battery Level: " + bicycle.getBattery_level() + "%");
        batteryLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #f0f0f0;");

        Label rangeLabel = new Label("📍 Range: " + bicycle.getRange_km() + " km");
        rangeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #f0f0f0;");

        Label lastUpdatedLabel = new Label("🕒 Last Updated: " + bicycle.getLast_updated());
        lastUpdatedLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #bbbbbb;");

        Button reserveButton = new Button("Reserve Bike");
        reserveButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14px 24px; " +
                        "-fx-min-width: 160px; " +
                        "-fx-background-radius: 14px; " +
                        "-fx-cursor: hand;"
        );
        reserveButton.setOnAction(e -> {
            reserveBike(bicycle, station);
            BicycleRental rental = new BicycleRental(
                    0, SessionManager.getInstance().getUser(), bicycle, station,
                    null, new Timestamp(System.currentTimeMillis()), null, 0, 0, 0
            );
            showReservationConfirmation(bicycle, rental);
            modalStage.close();
        });

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff4444; " + // Red background
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14px 24px; " +
                        "-fx-min-width: 160px; " +
                        "-fx-background-radius: 14px; " +
                        "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> modalStage.close());

        HBox buttonLayout = new HBox(20, reserveButton, closeButton);
        buttonLayout.setAlignment(Pos.CENTER_LEFT);

        textLayout.getChildren().addAll(titleLabel, batteryLabel, rangeLabel, lastUpdatedLabel, buttonLayout);

        Image bikeIcon = new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm());
        ImageView bikeIconView = new ImageView(bikeIcon);
        bikeIconView.setFitHeight(140);
        bikeIconView.setFitWidth(140);
        bikeIconView.setPreserveRatio(true);

        modalLayout.getChildren().addAll(textLayout, bikeIconView);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 600, 400);
        modalScene.setFill(Color.TRANSPARENT); // Keep scene transparent

        // Make the window draggable
        final double[] dragDelta = new double[2];
        modalStage.setOpacity(1.0);
        modalStage.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            dragDelta[0] = mouseEvent.getScreenX() - modalStage.getX();
            dragDelta[1] = mouseEvent.getScreenY() - modalStage.getY();
        });
        modalStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            modalStage.setX(mouseEvent.getScreenX() - dragDelta[0]);
            modalStage.setY(mouseEvent.getScreenY() - dragDelta[1]);
        });

        modalStage.setScene(modalScene);
        modalStage.show();
        openModals.add(modalStage);
    }


    private void showReservationConfirmation(Bicycle bicycle, BicycleRental rental) {
        Stage modalStage = new Stage();
        modalStage.initStyle(StageStyle.TRANSPARENT); // Keep transparent background
        modalStage.setTitle("Reservation Confirmation");

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox modalLayout = new VBox(20); // Increased spacing
        modalLayout.setPadding(new Insets(40)); // Increased padding
        modalLayout.setStyle(
                "-fx-background-color: #2c2c2c; " +
                        "-fx-background-radius: 20px;" // Removed shadow effect
        );

        Label titleLabel = new Label("Reservation Confirmation");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label timerLabel = new Label("Time remaining: 10:00");
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Button pickUpButton = new Button("Pick Up");
        pickUpButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14px 24px; " +
                        "-fx-min-width: 160px; " +
                        "-fx-background-radius: 14px; " +
                        "-fx-cursor: hand;"
        );
        pickUpButton.setOnAction(e -> {
            stopTimerAndPickUpBike(bicycle, rental, modalStage);
        });

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff4444; " + // Red background
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 14px 24px; " +
                        "-fx-min-width: 160px; " +
                        "-fx-background-radius: 14px; " +
                        "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> modalStage.close());

        HBox buttonLayout = new HBox(20, pickUpButton, closeButton);
        buttonLayout.setAlignment(Pos.CENTER);

        modalLayout.getChildren().addAll(titleLabel, timerLabel, buttonLayout);
        stackPane.getChildren().add(modalLayout);

        Scene modalScene = new Scene(stackPane, 400, 250);
        modalScene.setFill(Color.TRANSPARENT); // Keep scene transparent

        // Make the window draggable
        final double[] dragDelta = new double[2];
        modalStage.setOpacity(1.0);
        modalStage.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            dragDelta[0] = mouseEvent.getScreenX() - modalStage.getX();
            dragDelta[1] = mouseEvent.getScreenY() - modalStage.getY();
        });
        modalStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            modalStage.setX(mouseEvent.getScreenX() - dragDelta[0]);
            modalStage.setY(mouseEvent.getScreenY() - dragDelta[1]);
        });

        modalStage.setScene(modalScene);
        modalStage.show();
        openModals.add(modalStage);

        startTimerDisplay(timerLabel, modalStage, bicycle, rental);
    }


    private void reserveBike(Bicycle bicycle, Station station) {
        try {
            bicycle.setStatus(Bicycle.STATUS.reserved);

            BicycleRentalService bicycleRentalService = new BicycleRentalService();
            BicycleRental rental = new BicycleRental(
                    0, SessionManager.getInstance().getUser(), bicycle, station,
                    null, new Timestamp(System.currentTimeMillis()), null, 0, 0, 0
            );
            bicycleRentalService.create(rental);

            BicycleService bicycleService = new BicycleService();
            bicycleService.update(bicycle);

            stationService.updateAvailableBikes(station, station.getAvailable_bikes() - 1);

            System.out.println("Bike at " + rental.getStart_station().getName() + "reserved successfully.");

            showReservationConfirmation(bicycle, rental);
            startReservationTimer(bicycle, station, rental);


        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Reservation Failed", "An error occurred while reserving the bike. Please try again.");
        }
    }

    private void reloadCurrentScene() {
        try {
            // Close all open modals
            for (Stage modal : openModals) {
                if (modal != null && modal.isShowing()) {
                    modal.close();
                }
            }
            openModals.clear(); // Clear the list of open modals

            // Reload the current scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/station/front/station.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) home_button.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Reload Failed", "An error occurred while reloading the page. Please try again.");
        }
    }

    private void startReservationTimer(Bicycle bicycle, Station station, BicycleRental rental) {
        int[] reservationDurationSeconds = {60};

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    reservationDurationSeconds[0]--;

                    if (reservationDurationSeconds[0] <= 0) {
                        cancelReservation(bicycle, station, rental);
                    } else {
                        System.out.println("Time remaining: " + reservationDurationSeconds[0] + " seconds");
                    }
                })
        );

        timeline.setCycleCount(reservationDurationSeconds[0]);
        timeline.play();
    }

    private void startTimerDisplay(Label timerLabel, Stage modalStage, Bicycle bicycle, BicycleRental rental) {
        // Stop any existing timeline
        if (reservationTimeline != null) {
            reservationTimeline.stop();
            reservationTimeline = null;
        }

        int[] reservationDurationSeconds = {60}; // 10 minutes

        reservationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    reservationDurationSeconds[0]--;

                    if (reservationDurationSeconds[0] <= 0) {
                        modalStage.close();
                        cancelReservation(bicycle, rental.getStart_station(), rental);
                    } else {
                        int minutes = reservationDurationSeconds[0] / 60;
                        int seconds = reservationDurationSeconds[0] % 60;
                        timerLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
                    }
                })
        );

        reservationTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely until stopped
        reservationTimeline.play();

        // Ensure the timeline is stopped when the modal is closed
        modalStage.setOnCloseRequest(event -> {
            if (reservationTimeline != null) {
                reservationTimeline.stop();
                reservationTimeline = null;
            }
        });
    }

    private void cancelReservation(Bicycle bicycle, Station station, BicycleRental rental) {
        try {
            bicycle.setStatus(Bicycle.STATUS.available);

            BicycleService bicycleService = new BicycleService();
            bicycleService.update(bicycle);

            stationService.updateAvailableBikes(station, station.getAvailable_bikes() + 1);

            BicycleRentalService bicycleRentalService = new BicycleRentalService();
            bicycleRentalService.delete(rental.getId()); // Assuming delete method takes an ID

            System.out.println("Reservation for Bike at " + rental.getStart_station().getName() + " has been canceled.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Cancellation Failed", "An error occurred while canceling the reservation. Please try again.");
        }
    }

    private void stopTimerAndPickUpBike(Bicycle bicycle, BicycleRental rental, Stage modalStage) {
        // Stop the reservation timeline
        if (reservationTimeline != null) {
            reservationTimeline.stop();
            reservationTimeline = null; // Clear the reference to avoid memory leaks
            System.out.println("Timer stopped."); // Debug statement
        }

        // Close the modal
        modalStage.close();

        // Update the bike status to "in use"
        try {
            bicycle.setStatus(Bicycle.STATUS.in_use);

            BicycleService bicycleService = new BicycleService();
            bicycleService.update(bicycle);

            System.out.println("Bike at " + rental.getStart_station().getName() + " picked up successfully.");

            // Reload the current scene to reflect changes
            reloadCurrentScene();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Pick Up Failed", "An error occurred while picking up the bike. Please try again.");
        }
    }

    private void addMarkersToMap(List<Station> stations) {
        StringBuilder script = new StringBuilder();

        URL iconUrl = getClass().getResource("/images/station/icons/mapStationIcon.png");
        if (iconUrl == null) {
            System.err.println("Icon file not found! Check the path: /images/station/icons/mapStationIcon.png");
        }
        else {
            String iconPath = iconUrl.toExternalForm();
            script.append(String.format(
                    "var customIcon = L.icon({ " +
                            "iconUrl: '%s', " +
                            "iconSize: [64, 64], " +
                            "iconAnchor: [16, 32], " +
                            "popupAnchor: [0, -32] " +
                            "}); ",
                    iconPath
            ));
        }

        for (Station station : stations) {
            String latitude = String.valueOf(station.getLocation().getLatitude());
            String longitude = String.valueOf(station.getLocation().getLongitude());
            String name = station.getName();

            script.append(String.format(
                    "var marker = L.marker([%s, %s], { icon: customIcon }).addTo(map); " +
                            "marker.bindPopup('<b>%s</b><br>Available Bikes: %d'); " +
                            "marker.on('click', function() { " +
                            "   if (window.JavaBridge) { " +
                            "       window.JavaBridge.handleMarkerClick('%s'); " +
                            "   } else { " +
                            "       console.error('JavaBridge is not defined!'); " +
                            "   } " +
                            "}); ",
                    latitude, longitude, name, station.getAvailable_bikes(), name
            ));
        }

        // Execute the script to add markers to the map
        webEngine.executeScript(script.toString());
    }

    public void search() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            loadStationsIntoFlowPane();
        } else {
            try {
                List<Station> stations = stationService.search("name", query);
                stationFlowPane.getChildren().clear();
                for (Station station : stations) {
                    VBox stationCard = createStationCard(station);
                    stationFlowPane.getChildren().add(stationCard);
                }
                if (stations.size() == 1) {
                    Station station = stations.get(0);
                    String latitude = String.valueOf(station.getLocation().getLatitude());
                    String longitude = String.valueOf(station.getLocation().getLongitude());
                    String script = String.format("map.setView([%s, %s], 15, {animate: true, duration: 2.0});", latitude, longitude);
                    webEngine.executeScript(script);
                }
            } catch (SQLException e) {
                showErrorDialog("Search Error", "An error occurred while searching for stations.");
                e.printStackTrace();
            }
        }
    }

}