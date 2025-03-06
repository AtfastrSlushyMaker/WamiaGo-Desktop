package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Booking;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService implements IService<Booking> {

    private final Connection connection;

    public BookingService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Booking booking) throws SQLException {
        String query = "INSERT INTO booking (id_trip, id_passenger, reserved_seats, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, booking.getIdTrip());
            stmt.setInt(2, booking.getIdPassenger());
            stmt.setInt(3, booking.getReservedSeats());
            stmt.setString(4, booking.getStatus().name());
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void update(Booking booking) throws SQLException {
        String query = "UPDATE booking SET id_trip = ?, id_passenger = ?, reserved_seats = ?, status = ? WHERE id_booking = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, booking.getIdTrip());
            stmt.setInt(2, booking.getIdPassenger());
            stmt.setInt(3, booking.getReservedSeats());
            stmt.setString(4, booking.getStatus().name());
            stmt.setInt(5, booking.getIdBooking());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM booking WHERE id_booking = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Booking> read() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM booking";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setIdBooking(rs.getInt("id_booking"));
                booking.setIdTrip(rs.getInt("id_trip"));
                booking.setIdPassenger(rs.getInt("id_passenger"));
                booking.setReservedSeats(rs.getInt("reserved_seats"));
                booking.setStatus(Booking.Status.valueOf(rs.getString("status")));
                bookings.add(booking);
            }
        }
        return bookings;
    }
}