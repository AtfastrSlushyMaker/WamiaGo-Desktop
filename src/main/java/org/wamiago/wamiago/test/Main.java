package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Booking;
import org.wamiago.wamiago.entities.Trip;
import org.wamiago.wamiago.services.BookingService;
import org.wamiago.wamiago.services.TripService;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataBase.getInstance();

        TripService tripService = new TripService();
        BookingService bookingService = new BookingService();

        try {
//            Trip trip = new Trip(0, "Tunis", "Sousse", new Date(), 50, 150.0, 2, 1);
//            tripService.create(trip);


            List<Trip> trips = tripService.read();
            for (Trip t : trips) {
                System.out.println(t.getIdTrip() + " " + t.getDepartureCity() + " " + t.getArrivalCity());
            }


//            Booking booking = new Booking(28, 1, 1, 2, "Confirmed");
//            bookingService.create(booking);


            List<Booking> bookings = bookingService.read();
            for (Booking b : bookings) {
                System.out.println(b.getIdBooking() + " " + b.getIdTrip() + " " + b.getIdPassenger() + " " + b.getReservedSeats() + " " + b.getStatus());
            }


            Booking updatedBooking = new Booking(3, 1, 1, 5, Booking.Status.Canceled);
            bookingService.update(updatedBooking);

//            bookingService.delete(2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}