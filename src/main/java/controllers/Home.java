package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ResizeHelper.ResizeHelper;

public class Home extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/user.front/loginSignup.fxml"));
        // Parent root = FXMLLoader.load(getClass().getResource("/dashboard/dashboard.fxml"));

        // Set up the scene
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UNDECORATED); // Use undecorated stage for custom window controls
        primaryStage.setScene(scene);
        primaryStage.setTitle("Wamia Go - Welcome!");

        // Get the screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Set the window size dynamically (e.g., 50% of screen width and height)
        double windowWidth = screenBounds.getWidth() * 0.6;
        double windowHeight = screenBounds.getHeight() * 0.8;
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);

        // Calculate the position to center the window
        double centerX = (screenBounds.getWidth() - windowWidth) / 2;
        double centerY = (screenBounds.getHeight() - windowHeight) / 2;
        primaryStage.setX(centerX);
        primaryStage.setY(centerY);

        // Set minimum and maximum window size
        primaryStage.setMinWidth(1024); // Minimum width
        primaryStage.setMinHeight(600); // Minimum height
        primaryStage.setMaxWidth(screenBounds.getWidth()); // Maximum width (full screen)
        primaryStage.setMaxHeight(screenBounds.getHeight()); // Maximum height (full screen)

        // Add resize listener for resizing the window
        ResizeHelper.addResizeListener(primaryStage);

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}