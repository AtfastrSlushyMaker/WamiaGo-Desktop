package controllers.taxi.adminside;

import entities.Ride;
import entities.Request;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import services.RideService;
import services.RequestService;

public class StatisticsRequestRide {

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

    private RideService rideService;
    private RequestService requestService;

    @FXML
    public void initialize() {
        rideService = new RideService();
        requestService = new RequestService();
        loadRideStats();
        loadRequestStats();
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
}
