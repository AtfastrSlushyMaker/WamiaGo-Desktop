package controllers;

import controllers.dashboard.DashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Home extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/dashboard/dashboard.fxml"));
        DashboardController dashboardController = new DashboardController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Dashboards");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}