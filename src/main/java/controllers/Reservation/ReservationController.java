package controllers.Reservation;

import entities.Reservation;
import entities.BicycleRental;
import entities.Station;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public class ReservationController {
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
    private FlowPane stationFlowPane;  // Using FlowPane for flexibility
    private ReservationService reservationService = new ReservationService();
    private final StationService stationService = new StationService(); // Fetch stations

    @FXML
    public void initialize() {
        root.getStylesheets().add(getClass().getResource("/Reservation/front/reservation.css").toExternalForm());
        loadStationsIntoFlowPane();
        setupNavigation();
    }

    private void setupNavigation() {
        home_button.setOnAction(event -> loadScene("/dashboard/dashboard.fxml"));
        rides_button.setOnAction(event -> loadScene("/rides/rides.fxml"));
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
            for (Reservation station : reservationService.read()) {
                VBox stationCard = createStationCard(station);
                stationFlowPane.getChildren().add(stationCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createStationCard(Reservation station) {
        VBox stationCard = new VBox(10);
        stationCard.setPadding(new Insets(10));
        stationCard.getStyleClass().add("station-card");
        stationCard.setAlignment(Pos.CENTER);

        // Create HBox for image and name
        HBox imageAndTextBox = createImageAndTextBox(station);

        // Station bike count
        Label localDate  = new Label("date: " + station.getDate());
        localDate.getStyleClass().add("station-bike-count");

        Label status  = new Label("status: " + station.getStatus());
        status.getStyleClass().add("station-bike-count");

        Label description  = new Label("description: " + station.getDescription());
        description.getStyleClass().add("station-bike-count");

        // Select button
        //Button selectButton = createSelectButton(station);

        // Assemble the card components
        stationCard.getChildren().addAll(imageAndTextBox, localDate,status,description);

        // Hover effect (zoom out)
        stationCard.setOnMouseExited(event -> {
            stationCard.setScaleX(1);  // Scale down the card to 95% of its original size (zoom out)
            stationCard.setScaleY(1);  // Scale down the card to 95% of its original size (zoom out)
        });

        // Revert to original size when hover is removed
        stationCard.setOnMouseEntered(event -> {
            stationCard.setScaleX(1.05);  // Reset to original size
            stationCard.setScaleY(1.05);  // Reset to original size
        });

        return stationCard;
    }


    private HBox createImageAndTextBox(Reservation station) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);  // Center align the elements
        hbox.setFillHeight(true);  // Allow the HBox to take up all vertical space if needed

        ImageView stationImage = new ImageView(new Image(getClass().getResource("/images/icons/public-transport_3061677.png").toExternalForm()));
        stationImage.setFitWidth(50);
        stationImage.setFitHeight(50);
        stationImage.setPreserveRatio(true);

        // Adjust text wrapping for the station name using Text instead of Label
        Text nameText = new Text(station.getDescription());
        nameText.setWrappingWidth(180);  // Set the wrapping width for the text
        nameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #FFFFFF;");


        // Allow text to grow and fill space
        HBox.setHgrow(nameText, Priority.ALWAYS);

        hbox.getChildren().addAll(stationImage, nameText);
        return hbox;
    }

//    private Button createSelectButton(Reservation station) {
//        Button selectButton = new Button("Select");
//        selectButton.getStyleClass().add("station-button");
//        selectButton.setOnAction(e -> openStationDetails(station));
//        return selectButton;
//    }

    private void openStationDetails(Station station) {
        System.out.println("Opening details for: " + station.getName());

        // Create a new Stage (popup/modal)
        Stage modalStage = new Stage();
        modalStage.setTitle("Available Bicycles at " + station.getName());

        // Dark overlay with some transparency
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Semi-transparent black overlay

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);"); // Dark background and shadow for modal

        // Title for the modal
        Label titleLabel = new Label("Available Bicycles");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;-fx-text-alignment: center;-fx-font-family: Inter");

        // FlowPane for available bicycles
        FlowPane bicycleFlowPane = new FlowPane();
        bicycleFlowPane.setHgap(10);
        bicycleFlowPane.setVgap(10);

        // Add bicycles to the FlowPane (and make them clickable)
       // addAvailableBicycles(bicycleFlowPane, station);

        // Close button for the modal
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modalStage.close());
        closeButton.getStyleClass().add("station-bike-close-button");
        // Create an HBox to center the button at the bottom
        HBox closeButtonContainer = new HBox();
        closeButtonContainer.setAlignment(Pos.CENTER);  // Center the button in the HBox
        closeButtonContainer.getChildren().add(closeButton);

        // Add components to the VBox
        modalLayout.getChildren().addAll(titleLabel, bicycleFlowPane, closeButtonContainer);

        stackPane.getChildren().add(modalLayout);  // Add the modal layout to the stack pane

        // Set up the Scene and Stage
        Scene modalScene = new Scene(stackPane, 400, 300);
        modalStage.setScene(modalScene);
        modalStage.show();
    }


//    private void addAvailableBicycles(FlowPane bicycleFlowPane, Station station) {
//        StationService stationService = new StationService();
//        for (Bicycle bicycle : stationService.getAvailableBikes(station)) {
//            Button bikeButton = new Button();
//
//            // Load the bicycle icon image
//            Image bikeIcon = new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm()); // Replace with correct path
//            ImageView bikeIconView = new ImageView(bikeIcon);
//
//            // Make the icon larger
//            bikeIconView.setFitHeight(40);  // Increase the height
//            bikeIconView.setFitWidth(40);   // Increase the width
//
//            // Set the button text alongside the icon
//            bikeButton.setGraphic(bikeIconView);
//            bikeButton.setText(" Bike " + bicycle.getStatus());  // Add ID or type as text next to the icon
//            bikeButton.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-font-family: Inter; -fx-font-size: 14px;");
//            // Add hover effect to zoom in on the bike button
//            bikeButton.setOnMouseEntered(event -> {
//                bikeButton.setScaleX(1.1);  // Zoom in the button
//                bikeButton.setScaleY(1.1);  // Zoom in the button
//            });
//
//            // Revert to original size when hover is removed
//            bikeButton.setOnMouseExited(event -> {
//                bikeButton.setScaleX(1);  // Reset to original size
//                bikeButton.setScaleY(1);  // Reset to original size
//            });
//
//            // Make the bike button clickable
//            bikeButton.setOnAction(e -> showBikeDetails(bicycle, station));
//
//            bicycleFlowPane.getChildren().add(bikeButton);  // Add bike button to the FlowPane
//        }
//    }
//
//
//    private void showBikeDetails(Bicycle bicycle, Station station) {
//        // Create a new Stage (popup/modal)
//        Stage modalStage = new Stage();
//        modalStage.setTitle("Bike Details: " + bicycle.getId());
//
//        // Dark overlay with some transparency
//        StackPane stackPane = new StackPane();
//        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);"); // Semi-transparent black overlay
//
//        // Main layout with HBox to arrange content horizontally
//        HBox modalLayout = new HBox(20);  // Horizontal layout with some space between elements
//        modalLayout.setPadding(new Insets(20));
//        modalLayout.setStyle("-fx-background-color: #333333; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, black, 20, 0.5, 0, 0);");
//
//        // Left side content: VBox for text information
//        VBox textLayout = new VBox(10);
//        textLayout.setStyle("-fx-text-fill: white;");
//
//        // Title for the modal
//        Label titleLabel = new Label("Bike Details");
//        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
//
//        // Bike ID label
//        Label bikeIdLabel = new Label("Bike ID: " + bicycle.getId());
//        bikeIdLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
//
//        // Battery level label
//        Label batteryLabel = new Label("Battery Level: " + bicycle.getBattery_level() + "%");
//        batteryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
//
//        // Range label
//        Label rangeLabel = new Label("Range: " + bicycle.getRange_km() + " km");
//        rangeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
//
//        // Last updated label
//        Label lastUpdatedLabel = new Label("Last Updated: " + bicycle.getLast_updated());
//        lastUpdatedLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
//
//        // Reserve button
//        Button reserveButton = new Button("Reserve Bike");
//        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px;");
//        reserveButton.setOnAction(e -> {
//            reserveBike(bicycle, station);
//            modalStage.close(); // Close modal after reserving
//        });
//
//        // Close button for the modal
//        Button closeButton = new Button("Close");
//        closeButton.setOnAction(e -> modalStage.close());
//        closeButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");
//
//        // Add all text components to the VBox
//        textLayout.getChildren().addAll(titleLabel, bikeIdLabel, batteryLabel, rangeLabel, lastUpdatedLabel, reserveButton, closeButton);
//
//        // Right side content: Image of the bicycle
//        ImageView bikeImageView = new ImageView(new Image(getClass().getResource("/images/station/icons/bicycle_top_view.png").toExternalForm())); // Replace with your bike image path
//        bikeImageView.setFitHeight(200);  // Set height for the image
//        bikeImageView.setPreserveRatio(true);  // Maintain aspect ratio of the image
//
//        // Add VBox and ImageView to the HBox
//        modalLayout.getChildren().addAll(textLayout, bikeImageView);
//
//        stackPane.getChildren().add(modalLayout);  // Add the modal layout to the stack pane
//
//        // Set up the Scene and Stage
//        Scene modalScene = new Scene(stackPane, 600, 400);  // Adjust width and height as needed
//        modalStage.setScene(modalScene);
//        modalStage.show();
//    }
//
//
//    private void reserveBike(Bicycle bicycle, Station station) {
//        // Logic to reserve the bike (e.g., mark the bike as reserved, update status in the database)
//        BicycleRentalService bicycleRentalService = new BicycleRentalService();
//        StationService stationService = new StationService();
//        BicycleService bicycleService = new BicycleService();
//        try {
//            bicycle.setStatus(Bicycle.STATUS.reserved);
//
//            bicycleRentalService.create(new BicycleRental(0,new UserService().getById(1), bicycle, station,null,new Timestamp(System.currentTimeMillis()), null, 0, 0, 0));
//            bicycleService.update(bicycle);
//            stationService.updateAvailableBikes(station,station.getAvailable_bikes()-1);
//            System.out.println("Bike " + bicycle.getId() + " reserved successfully.");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

