package controllers.StaticDash;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import services.DashboardService;

public class StatisticsController {
    @FXML
    private Text adText; // Users
    @FXML
    private Text donsText; // Announcements
    @FXML
    private Text necessiteuxText; // Relocations
    @FXML
    private Text famillesText; // Reservations
    @FXML
    private PieChart pie;
    @FXML
    private BarChart<String, Number> myBarChart;

    private DashboardService dashboardService;

    @FXML
    public void initialize() {
        dashboardService = new DashboardService();
        loadStaticStats();
    }

    private void loadStaticStats() {
        // Fetch data from the service
        int totalUsers = dashboardService.getTotalUsers();
        int totalAnnouncements = dashboardService.getTotalAnnouncements();
        int totalRelocations = dashboardService.getTotalRelocations();
        int totalReservations = dashboardService.getTotalReservations();
        int ongoingReservation = dashboardService.getOngoingReservation();
        int completedReservation = dashboardService.getCompletedReservation();
        int canceledReservation = dashboardService.getCanceledReservation();
        int confirmedReservation = dashboardService.getCONFIRMEDReservation();


        adText.setText(String.valueOf(totalUsers));
        donsText.setText(String.valueOf(totalAnnouncements));
        necessiteuxText.setText(String.valueOf(totalRelocations));
        famillesText.setText(String.valueOf(totalReservations));


        double total = totalReservations;
        double ongoingPercentage = (total > 0) ? (ongoingReservation / total) * 100 : 0;
        double completedPercentage = (total > 0) ? (completedReservation / total) * 100 : 0;
        double canceledPercentage = (total > 0) ? (canceledReservation / total) * 100 : 0;
        double confirmedPercentage = (total > 0) ? (confirmedReservation / total) * 100 : 0;


        pie.getData().clear();
        pie.getData().addAll(
                new PieChart.Data(String.format("Ongoing (%.1f%%)", ongoingPercentage), ongoingReservation),
                new PieChart.Data(String.format("Completed (%.1f%%)", completedPercentage), completedReservation),
                new PieChart.Data(String.format("Canceled (%.1f%%)", canceledPercentage), canceledReservation),
                new PieChart.Data(String.format("Confirmed (%.1f%%)", confirmedPercentage), confirmedReservation)
        );


        myBarChart.getData().clear();
        BarChart.Series<String, Number> reservationStatusSeries = new BarChart.Series<>();
        reservationStatusSeries.getData().add(new BarChart.Data<>("Ongoing", ongoingReservation));
        reservationStatusSeries.getData().add(new BarChart.Data<>("Completed", completedReservation));
        reservationStatusSeries.getData().add(new BarChart.Data<>("Canceled", canceledReservation));
        reservationStatusSeries.getData().add(new BarChart.Data<>("Confirmed", confirmedReservation));
        myBarChart.getData().add(reservationStatusSeries);
    }
}