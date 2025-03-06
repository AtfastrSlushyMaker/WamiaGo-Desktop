package controllers.taxi.adminside;

import entities.Ride;
import entities.Request;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.RideService;
import services.RequestService;

public class StatisticsRequestRide {

    @FXML
    private LineChart<Number, Number> rideRequestLineChart;
    @FXML
    private Text totalRidesText;  // Total rides
    @FXML
    private Text completedRidesText; // Completed rides
    @FXML
    private Text ongoingRidesText; // Ongoing rides
    @FXML
    private Text canceledRidesText; // Canceled rides
    @FXML
    private Text totalRequestsText;  // Total requests
    @FXML
    private Text pendingRequestsText;  // Pending requests
    @FXML
    private Text acceptedRequestsText;  // Accepted requests
    @FXML
    private Text canceledRequestsText;  // Canceled requests
    @FXML
    private PieChart rideStatusPieChart;  // Pie chart for ride statuses
    @FXML
    private PieChart requestStatusPieChart;  // Pie chart for request statuses
    @FXML
    private BarChart<String, Number> rideStatusBarChart;  // Bar chart for ride statuses
    @FXML
    private BarChart<String, Number> requestStatusBarChart;  // Bar chart for request statuses
    // Line chart for ride requests
    @FXML
    private VBox totalRequestsCard;
    @FXML
    private VBox totalRidesCard;

    private RideService rideService;
    private RequestService requestService;

    @FXML
    public void initialize() {
        rideService = new RideService();
        requestService = new RequestService();
        loadRideStats();
        loadRequestStats();
        animateCard(totalRequestsCard);
        animateCard(totalRidesCard);
        loadRideRequestStats();
    }

    private void loadRideStats() {
        try {
            int totalRides = rideService.countRides();
            int completedRides = rideService.getByStatus(Ride.Status.COMPLETED).size();
            int ongoingRides = rideService.getByStatus(Ride.Status.ONGOING).size();
            int canceledRides = rideService.getByStatus(Ride.Status.CANCELED).size();

            // Set text labels
            totalRidesText.setText(String.valueOf(totalRides));
            completedRidesText.setText(String.valueOf(completedRides));
            ongoingRidesText.setText(String.valueOf(ongoingRides));
            canceledRidesText.setText(String.valueOf(canceledRides));

            // Pie chart data
            double total = totalRides;
            double ongoingPercentage = (total > 0) ? (ongoingRides / total) * 100 : 0;
            double completedPercentage = (total > 0) ? (completedRides / total) * 100 : 0;
            double canceledPercentage = (total > 0) ? (canceledRides / total) * 100 : 0;

            rideStatusPieChart.getData().clear();
            rideStatusPieChart.getData().addAll(
                    new PieChart.Data(String.format("Ongoing (%.1f%%)", ongoingPercentage), ongoingRides),
                    new PieChart.Data(String.format("Completed (%.1f%%)", completedPercentage), completedRides),
                    new PieChart.Data(String.format("Canceled (%.1f%%)", canceledPercentage), canceledRides)
            );

            // Bar chart data
            rideStatusBarChart.getData().clear();
            BarChart.Series<String, Number> rideStatusSeries = new BarChart.Series<>();
            rideStatusSeries.getData().add(new BarChart.Data<>("Ongoing", ongoingRides));
            rideStatusSeries.getData().add(new BarChart.Data<>("Completed", completedRides));
            rideStatusSeries.getData().add(new BarChart.Data<>("Canceled", canceledRides));
            rideStatusBarChart.getData().add(rideStatusSeries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRequestStats() {
        try {
            int totalRequests = requestService.countRequests();
            int pendingRequests = requestService.getByStatus(Request.RequestStatus.PENDING).size();
            int acceptedRequests = requestService.getByStatus(Request.RequestStatus.ACCEPTED).size();
            int canceledRequests = requestService.getByStatus(Request.RequestStatus.CANCELED).size();

            // Set text labels
            totalRequestsText.setText(String.valueOf(totalRequests));
            pendingRequestsText.setText(String.valueOf(pendingRequests));
            acceptedRequestsText.setText(String.valueOf(acceptedRequests));
            canceledRequestsText.setText(String.valueOf(canceledRequests));

            // Pie chart data for requests
            double totalRequest = totalRequests;
            double pendingPercentage = (totalRequest > 0) ? (pendingRequests / totalRequest) * 100 : 0;
            double acceptedPercentage = (totalRequest > 0) ? (acceptedRequests / totalRequest) * 100 : 0;
            double canceledPercentage = (totalRequest > 0) ? (canceledRequests / totalRequest) * 100 : 0;

            requestStatusPieChart.getData().clear();
            requestStatusPieChart.getData().addAll(
                    new PieChart.Data(String.format("Pending (%.1f%%)", pendingPercentage), pendingRequests),
                    new PieChart.Data(String.format("Accepted (%.1f%%)", acceptedPercentage), acceptedRequests),
                    new PieChart.Data(String.format("Canceled (%.1f%%)", canceledPercentage), canceledRequests)
            );

            // Bar chart data for requests
            requestStatusBarChart.getData().clear();
            BarChart.Series<String, Number> requestStatusSeries = new BarChart.Series<>();
            requestStatusSeries.getData().add(new BarChart.Data<>("Pending", pendingRequests));
            requestStatusSeries.getData().add(new BarChart.Data<>("Accepted", acceptedRequests));
            requestStatusSeries.getData().add(new BarChart.Data<>("Canceled", canceledRequests));
            requestStatusBarChart.getData().add(requestStatusSeries);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRideRequestStats() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Request per day");

        // Exemple de données pour plusieurs jours
        series.getData().add(new XYChart.Data<>(1, 30)); // Jour 1 - 30 demandes
        series.getData().add(new XYChart.Data<>(2, 50)); // Jour 2 - 50 demandes
        series.getData().add(new XYChart.Data<>(3, 40)); // Jour 3 - 40 demandes
        series.getData().add(new XYChart.Data<>(4, 60)); // Jour 4 - 60 demandes

        rideRequestLineChart.getData().clear();
        rideRequestLineChart.getData().add(series);
    }

    private void animateCard(VBox card) {
        // Animation de fondu (Opacity de 0 à 1)
        FadeTransition fade = new FadeTransition(Duration.millis(800), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Animation de translation (Effet "glissé" depuis le bas)
        TranslateTransition translate = new TranslateTransition(Duration.millis(800), card);
        translate.setFromY(20);
        translate.setToY(0);

        // Lancer les animations ensemble
        fade.play();
        translate.play();
    }
    @FXML
    public void handleShowGraphClick() {
        // Créer les axes pour le graphique (si vous n'en avez pas déjà)
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Request");

        // Créer le LineChart
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);

        // Créer une série de données fictives pour le graphique
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Request per day");
        series.getData().add(new XYChart.Data<>(1, 30));
        series.getData().add(new XYChart.Data<>(2, 50));
        series.getData().add(new XYChart.Data<>(3, 40));
        series.getData().add(new XYChart.Data<>(4, 60));

        // Ajouter la série de données au graphique
        chart.getData().add(series);

        // Créer un VBox pour contenir le graphique
        VBox vbox = new VBox(chart);

        // Créer un stage (fenêtre modale) pour afficher le graphique
        Stage modalStage = new Stage();
        modalStage.setTitle("Request Graphique ");
        modalStage.setScene(new Scene(vbox, 600, 400));
        modalStage.show();
    }
}
