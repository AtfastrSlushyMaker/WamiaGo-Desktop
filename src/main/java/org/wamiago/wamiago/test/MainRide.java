package org.wamiago.wamiago.test;

import entities.Ride;
import org.wamiago.wamiago.services.RideService;
import java.sql.Timestamp;
import java.util.Date;

public class MainRide {
    public static void main(String[] args) {
        RideService rideService = new RideService();

        try {

            Ride newRide = new Ride(0, 27, 30, 1, 15.5, 30, 25.0, Ride.Status.Ongoing, new Timestamp(new Date().getTime()));
            rideService.create(newRide);
            System.out.println("New ride created.");


            System.out.println("\nAll rides:");
            rideService.read().forEach(ride -> {
                System.out.println("Ride ID: " + ride.getIdRide());
                System.out.println("Taxi ID: " + ride.getIdTaxi());
                System.out.println("Client ID: " + ride.getIdClient());
                System.out.println("Request ID: " + ride.getIdRequest());
                System.out.println("Distance: " + ride.getDistance());
                System.out.println("Duration: " + ride.getDuration());
                System.out.println("Price: " + ride.getPrice());
                System.out.println("Status: " + ride.getStatus());
                System.out.println("Ride Date: " + ride.getRideDate());
                System.out.println("-----------------------------------");
            });


            Ride updatedRide = new Ride(newRide.getIdRide(), 27, 30, 1, 20.0, 35, 30.0, Ride.Status.Canceled, new Timestamp(new Date().getTime()));
            rideService.update(updatedRide);
            System.out.println("\nRide updated.");


            rideService.delete(newRide.getIdRide());
            System.out.println("\nRide deleted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
