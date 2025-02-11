package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.services.DriverService;
import org.wamiago.wamiago.services.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        DriverService driverService = new DriverService();

        try {

            Location location = new Location(1, "123 Main St", 36.8065f, 10.1815f);

            User newUser = new User("John Doe", "john@example.com", "15678", "securepassword", User.Role.CLIENT, location);
            User otherUser = new User("Ahmed", "Ali", "1", "passs", User.Role.CLIENT, location);

            userService.addUser(newUser);
            userService.addUser(otherUser);

            Driver driver = new Driver(otherUser.getId(), otherUser.getName(), otherUser.getEmail(), otherUser.getPhone(), otherUser.getPassword(), Driver.DriverRole.TAXI_DRIVER,otherUser.getLocation(),111,1);

            driverService.addDriver(driver);

            System.out.println("User and Driver added successfully!");

            System.out.println("All users:");
            userService.getAllUsers().forEach(System.out::println);

            System.out.println("All drivers:");
            driverService.getAllDrivers().forEach(System.out::println);

            newUser.setName("John Updated");
            userService.updateUser(newUser);
            System.out.println("User updated!");

            User retrievedUser = userService.getUserById(newUser.getId());
            System.out.println("Retrieved User: " + retrievedUser);

            driver.setName("Ahmed Updated");
            driverService.updateDriver(driver);
            System.out.println("Driver updated!");

            Driver retrievedDriver = driverService.getDriverById(driver.getId());
            System.out.println("Retrieved Driver: " + retrievedDriver);

            userService.deleteUser(newUser.getId());
            driverService.deleteDriver(driver.getId());
            System.out.println("User and Driver deleted!");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


