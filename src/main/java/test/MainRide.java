package test;

import services.RequestService;
import services.DriverService;
import services.RideService;
import entities.Request;
import entities.Driver;
import entities.Ride;

import java.sql.SQLException;
import java.sql.Timestamp;

public class MainRide {
    public static void main(String[] args) {
        try {
            // Initialisation des services
            RequestService requestService = new RequestService();
            DriverService driverService = new DriverService();
            RideService rideService = new RideService();

            // Récupération d'une requête existante dans la base de données
            int requestId = 7; // Remplace par un ID existant
            Request request = requestService.getById(requestId);
            if (request == null) {
                System.out.println("❌ Requête non trouvée !");
                return;
            }

            // Récupération d'un chauffeur existant dans la base de données
            int driverId = 1; // Remplace par un ID existant
            Driver driver = driverService.getById(driverId);
            if (driver == null) {
                System.out.println("❌ Chauffeur non trouvé !");
                return;
            }

            // Création d'un nouvel objet Ride
            Ride ride = new Ride();
            ride.setRequest(request);
            ride.setDriver(driver);
            ride.setDuration(30); // Exemple : 30 minutes
            ride.setPrice(15.0);  // Exemple : 15.0 unités monétaires
            ride.setStatus(Ride.Status.ONGOING); // Exemple de statut
            ride.setRideDate(new Timestamp(System.currentTimeMillis())); // Heure actuelle

            // Appel de la méthode create pour insérer la course
            rideService.create(ride);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
