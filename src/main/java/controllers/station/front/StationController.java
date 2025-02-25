    package controllers.station.front;

    import entities.Bicycle;
    import javafx.animation.KeyFrame;
    import javafx.animation.Timeline;
    import javafx.concurrent.Worker;
    import javafx.scene.control.*;
    import javafx.scene.web.WebEngine;
    import javafx.scene.web.WebView;
    import javafx.util.Duration;
    import entities.BicycleRental;
    import entities.Station;
    import javafx.application.Platform;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.geometry.Insets;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.*;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;
    import services.BicycleRentalService;
    import services.BicycleService;
    import services.StationService;
    import javafx.geometry.Pos;
    import javafx.scene.image.Image;
    import javafx.scene.layout.VBox;
    import utils.SessionManager;

    import java.io.IOException;
    import java.net.URL;
    import java.sql.SQLException;
    import java.sql.Timestamp;
    import java.util.ArrayList;
    import java.util.List;

    public class StationController {
        @FXML
        private Button bookings_button;
        @FXML
        private Button history_button;
        @FXML
        private Button home_button;
        @FXML
        private Button logout_button;
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

        private WebEngine webEngine;

        private final StationService stationService = new StationService();
        private final List<Stage> openModals = new ArrayList<>();
        private Timeline reservationTimeline;

        @FXML  public void initialize() {
            root.getStylesheets().add(getClass().getResource("/station/front/station.css").toExternalForm());
            loadStationsIntoFlowPane();
            setupNavigation();
            loadMap();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
        private void loadMap() {
            webEngine = map.getEngine();
            String path = getClass().getResource("/maps/map.html").toExternalForm();
            webEngine.load(path);
            map.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    // Map is loaded, now add markers
                    try {
                        addMarkersToMap(stationService.read());
                    }// stations is your list of station data
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
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
                    List<Station> stations = stationService.read();
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

            if (station.getStatus() == Station.STATUS.disabled || new StationService().getAvailableBikes(station).size()==0 || station.getAvailable_docks() == 0) {
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
            System.out.println("Opening details for: " + station.getName());

            openModals.add(modalStage);
            modalStage.setTitle("Available Bicycles at " + station.getName());

            StackPane stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

            VBox modalLayout = new VBox(10);
            modalLayout.setPadding(new Insets(20));
            modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");

            Label titleLabel = new Label("Available Bicycles");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;-fx-text-alignment: center;-fx-font-family: Inter");

            FlowPane bicycleFlowPane = new FlowPane();
            bicycleFlowPane.setHgap(10);
            bicycleFlowPane.setVgap(10);

            addAvailableBicycles(bicycleFlowPane, station);

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> modalStage.close());
            closeButton.getStyleClass().add("station-bike-close-button");

            HBox closeButtonContainer = new HBox();
            closeButtonContainer.setAlignment(Pos.CENTER);
            closeButtonContainer.getChildren().add(closeButton);

            modalLayout.getChildren().addAll(titleLabel, bicycleFlowPane, closeButtonContainer);
            stackPane.getChildren().add(modalLayout);

            Scene modalScene = new Scene(stackPane, 400, 300);
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
                Button bikeButton = new Button();

                Image bikeIcon = new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm());
                ImageView bikeIconView = new ImageView(bikeIcon);
                bikeIconView.setFitHeight(40);
                bikeIconView.setFitWidth(40);

                bikeButton.setGraphic(bikeIconView);
                bikeButton.setText(" Bike " + bicycle.getStatus());
                bikeButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-font-family: Inter; -fx-font-size: 14px;");

                bikeButton.setOnMouseEntered(event -> {
                    bikeButton.setScaleX(1.1);
                    bikeButton.setScaleY(1.1);
                });

                bikeButton.setOnMouseExited(event -> {
                    bikeButton.setScaleX(1);
                    bikeButton.setScaleY(1);
                });

                bikeButton.setOnAction(e -> showBikeDetails(bicycle, station));
                bicycleFlowPane.getChildren().add(bikeButton);
            }
        }

        private void showBikeDetails(Bicycle bicycle, Station station) {
            Stage modalStage = new Stage();
            modalStage.setTitle("Bike Details: at " + station.getName());

            StackPane stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

            HBox modalLayout = new HBox(20);
            modalLayout.setPadding(new Insets(20));
            modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");

            VBox textLayout = new VBox(10);
            textLayout.setStyle("-fx-text-fill: white;");

            Label titleLabel = new Label("Bike Details");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
            titleLabel.setAlignment(Pos.CENTER);
            Label batteryLabel = new Label("Battery Level: " + bicycle.getBattery_level() + "%");
            batteryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            Label rangeLabel = new Label("Range: " + bicycle.getRange_km() + " km");
            rangeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            Label lastUpdatedLabel = new Label("Last Updated: " + bicycle.getLast_updated());
            lastUpdatedLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            Button reserveButton = new Button("Reserve Bike");
            reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px;");
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
            closeButton.setOnAction(e -> modalStage.close());
            closeButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

            textLayout.getChildren().addAll(titleLabel,batteryLabel, rangeLabel, lastUpdatedLabel, reserveButton, closeButton);

            Image bikeIcon = new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm());
            ImageView bikeIconView = new ImageView(bikeIcon);
            bikeIconView.setFitHeight(100);
            bikeIconView.setFitWidth(100);
            bikeIconView.setPreserveRatio(true);

            modalLayout.getChildren().addAll(textLayout, bikeIconView);
            stackPane.getChildren().add(modalLayout);

            Scene modalScene = new Scene(stackPane, 400, 300);
            modalStage.setScene(modalScene);
            modalStage.show();
            openModals.add(modalStage);
        }

        private void showReservationConfirmation(Bicycle bicycle, BicycleRental rental) {
            Stage modalStage = new Stage();
            modalStage.setTitle("Reservation Confirmation");

            StackPane stackPane = new StackPane();
            stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

            VBox modalLayout = new VBox(10);
            modalLayout.setPadding(new Insets(20));
            modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");

            Label titleLabel = new Label("Reservation Confirmation");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

            Label timerLabel = new Label("Time remaining: 10:00");
            timerLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            Button pickUpButton = new Button("Pick Up");
            pickUpButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px;");
            pickUpButton.setOnAction(e -> {
                stopTimerAndPickUpBike(bicycle, rental, modalStage);
            });

            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> modalStage.close());
            closeButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

            modalLayout.getChildren().addAll(titleLabel,timerLabel, pickUpButton, closeButton);
            stackPane.getChildren().add(modalLayout);

            Scene modalScene = new Scene(stackPane, 300, 200);
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

                System.out.println("Bike at "+rental.getStart_station().getName()+ "reserved successfully.");

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
            int[] reservationDurationSeconds = {1*60};

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

            int[] reservationDurationSeconds = {1 * 60}; // 10 minutes

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

                System.out.println("Reservation for Bike at "+rental.getStart_station().getName()+" has been canceled.");
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

                System.out.println("Bike at "+rental.getStart_station().getName()+" picked up successfully.");

                // Reload the current scene to reflect changes
                reloadCurrentScene();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Pick Up Failed", "An error occurred while picking up the bike. Please try again.");
            }
        }

        public void addMarkersToMap(List<Station> stations) {
            for (Station station : stations) {
                String latitude = String.valueOf(station.getLocation().getLatitude());
                String longitude = String.valueOf(station.getLocation().getLongitude());
                String name = station.getName();

                URL imageUrl = getClass().getResource("/images/station/icons/mapStationIcon.png");
                if (imageUrl != null) {
                    String imagePath = imageUrl.toString();
                    String script = String.format(
                            "var customIcon = L.icon({ " +
                                    "iconUrl: '%s', " +
                                    "iconSize: [64, 64], " +
                                    "iconAnchor: [16, 32], " +
                                    "popupAnchor: [0, -32] " +
                                    "}); " +
                                    "L.marker([%s, %s], { icon: customIcon }).addTo(map).bindPopup('%s');",
                            imagePath, latitude, longitude, name);
                    webEngine.executeScript(script);
                } else {
                    System.out.println("Image not found!");
                }

            }
        }


    }