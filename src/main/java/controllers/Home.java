package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import services.ClimatiqService;
import services.TrafficTimeEstimator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class Home extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/user.front/loginSignup.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/trips/back_trip.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Wamia Go - Welcome!");
        primaryStage.setResizable(false);
        primaryStage.show();

        //TrafficTimeEstimator estimator = new TrafficTimeEstimator("DW9egp1lljrp_9klXkmSp8y-SuoywTOGIspZgdGCGlg");
        String apiKey = "R3V5TWVV3S7CZ9F3XZWEMKQF8M";
        ClimatiqService tester = new ClimatiqService(apiKey);

        String apiKey2 = "R3V5TWVV3S7CZ9F3XZWEMKQF8M";
        ClimatiqService service = new ClimatiqService(apiKey2);

        try {

            JSONObject result = service.calculateElectricityEmissions(100);
            System.out.println("Emission Results:\n" + result.toString(2));

            // Test car emissions (50 kW power for 2 hours, electric)
            JSONObject carResult = service.calculateCarEmissions(50, 2, "electric");
            System.out.println("\nCar Emissions:\n" + carResult.toString(2));



//            String duration = estimator.calculateTravelTime("Bizerte", "Ariana");
//           System.out.println("Travel Time: " + duration);

        } catch (Exception e) {
            System.err.println("Error calculating travel time: " + e.getMessage());
        }


    }

}