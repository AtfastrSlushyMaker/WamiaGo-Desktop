package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ResizeHelper.*;

public class Home extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/user.front/loginSignup.fxml"));

        // Set up the scene with a fixed size
        Scene scene = new Scene(root, 1024, 800);

        // Initialize the stage
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Wamia Go - Welcome!");

        // Add resize listener
        ResizeHelper.addResizeListener(primaryStage);

        // Set minimum window size (optional)
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(600);

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}