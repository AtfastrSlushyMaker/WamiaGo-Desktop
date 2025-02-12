package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.*;
import org.wamiago.wamiago.services.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        IService<User> userService = new UserService();
        IService<Driver> driverService = new DriverService();
        RatingService ratingService = new RatingService();

        try {
            System.out.println("### =========================== ###");
            System.out.println("###        Creating Users       ###");
            System.out.println("### =========================== ###");
            Location location = new Location(1, "123 Main St", 36.8065f, 10.1815f);

            User newUser = new User(1, "John Doe", "john@example.com", "15678", "securepassword", User.Role.CLIENT,
                    location, User.Gender.MALE, "john_profile.jpg", true, User.AccountStatus.ACTIVE,
                    LocalDate.of(1990, 5, 15), User.Status.OFFLINE);
            User otherUser = new User(2, "Ahmed Ali", "ahmed@example.com", "123456", "passs", User.Role.CLIENT,
                    location, User.Gender.MALE, "ahmed_profile.jpg", false, User.AccountStatus.DEACTIVATED,
                    LocalDate.of(1985, 7, 20), User.Status.OFFLINE);

            userService.create(newUser);
            userService.create(otherUser);

            System.out.println("Created User: " + newUser);
            System.out.println("Created User: " + otherUser);

            System.out.println("### =========================== ###");
            System.out.println("###        Creating Driver      ###");
            System.out.println("### =========================== ###");

            Driver driver = new Driver(
                    1,
                    newUser.getId(),
                    "John Doe",
                    "john@example.com",
                    "15678",
                    "securepassword",
                    Driver.DriverRole.TAXI_DRIVER,
                    location,
                    "A1B2C3D4",
                    Driver.DRIVER_ACTIVE,
                    User.Gender.MALE,
                    "john_profile.jpg",
                    true,
                    User.AccountStatus.ACTIVE,
                    LocalDate.of(1990, 5, 15),
                    User.Status.OFFLINE
            );

            driverService.create(driver);
            System.out.println("Created Driver: " + driver);

            System.out.println("### =========================== ###");
            System.out.println("###         Creating Rating     ###");
            System.out.println("### =========================== ###");
            Rating newRating = new Rating(1, newUser.getId(), driver.getIdDriver(), "Great driver!", 5);
            ratingService.create(newRating);
            System.out.println("Rating added: " + newRating);

            System.out.println("### =========================== ###");
            System.out.println("###     Reading All Ratings     ###");
            System.out.println("### =========================== ###");
            List<Rating> ratings = ratingService.read();
            System.out.println("All Ratings:");
            ratings.forEach(System.out::println);

            System.out.println("### =========================== ###");
            System.out.println("###   Reading Ratings by Driver ###");
            System.out.println("### =========================== ###");
            List<Rating> driverRatings = ratingService.getRatingsByDriver(driver.getIdDriver());
            System.out.println("Driver " + driver.getIdDriver() + " Ratings: " + driverRatings);

            System.out.println("### =========================== ###");
            System.out.println("###    Calculating Avg Rating   ###");
            System.out.println("### =========================== ###");
            double avgRating = ratingService.getAverageRatingByDriver(driver.getIdDriver());
            System.out.println("Driver " + driver.getIdDriver() + " Average Rating: " + avgRating);

            System.out.println("### =========================== ###");
            System.out.println("###       Reading All Users     ###");
            System.out.println("### =========================== ###");
            userService.read().forEach(System.out::println);

            System.out.println("### =========================== ###");
            System.out.println("###      Reading All Drivers    ###");
            System.out.println("### =========================== ###");
            driverService.read().forEach(System.out::println);

            System.out.println("### =========================== ###");
            System.out.println("###     Updating User and Driver ###");
            System.out.println("### =========================== ###");
            newUser.setName("John Updated");
            userService.update(newUser);
            System.out.println("Updated User: " + newUser);

            driver.setName("Ahmed Updated");
            driverService.update(driver);
            System.out.println("Updated Driver: " + driver);

            System.out.println("### =========================== ###");
            System.out.println("###  Retrieving Updated User and Driver ###");
            System.out.println("### =========================== ###");
            User retrievedUser = userService.getById(newUser.getId());
            System.out.println("Retrieved User: " + retrievedUser);

            Driver retrievedDriver = driverService.getById(driver.getId());
            System.out.println("Retrieved Driver: " + retrievedDriver);

            System.out.println("### =========================== ###");
            System.out.println("###     Deleting User and Driver ###");
            System.out.println("### =========================== ###");

            System.out.println("Deleting Driver with ID: " + driver.getId());
            System.out.println("Deleting User with ID: " + newUser.getId());

            driverService.delete(driver.getId());

            userService.delete(newUser.getId());
            userService.delete(otherUser.getId());

            System.out.println("User and Driver deleted!");
        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}