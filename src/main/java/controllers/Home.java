package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Home extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/announcements.fxml"));
//        primaryStage.setTitle("Gestion des Annonces");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Reservation/Front/Reservations.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}