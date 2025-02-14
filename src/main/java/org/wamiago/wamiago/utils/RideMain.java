package org.wamiago.wamiago.utils;

import org.wamiago.wamiago.entities.Ride;
import org.wamiago.wamiago.services.RideService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class RideMain {
    public static void main(String[] args) {
        RideService rideService = new RideService();

        try {
            // CREATE: Add a new ride
            Ride newRide = new Ride(
                    0, // L'ID est auto-incrémenté, donc on passe 0
                    1, // id_taxi
                    2, // id_client
                    3, // id_request
                    12.5, // distance
                    25, // duration
                    20.0, // price
                    Ride.Status.ONGOING, // statut
                    new Timestamp(System.currentTimeMillis()) // ride_date (date actuelle)
            );

            rideService.create(newRide);
            System.out.println("Ride created successfully!");

            // READ: Fetch all rides
            List<Ride> rides = rideService.read();
            System.out.println("List of rides:");
            for (Ride ride : rides) {
                System.out.println(ride);
            }

            // UPDATE: Update a ride
            if (!rides.isEmpty()) {
                Ride rideToUpdate = rides.get(0); // Update the first ride
                rideToUpdate.setPrice(25.0); // Modify the price
                rideToUpdate.setStatus(Ride.Status.COMPLETED); // Change status
                rideService.update(rideToUpdate);
                System.out.println("Ride updated successfully!");
            }

            // DELETE: Delete a ride (if exists)
            if (!rides.isEmpty()) {
                int idToDelete = rides.get(0).getIdRide(); // Get the first ride's ID
                rideService.delete(idToDelete);
                System.out.println("Ride deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
