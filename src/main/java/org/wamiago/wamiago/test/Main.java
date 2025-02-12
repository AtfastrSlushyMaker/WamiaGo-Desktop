package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.services.DriverService;
import org.wamiago.wamiago.services.IService;
import org.wamiago.wamiago.services.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        IService<User> userService = new UserService();
        IService<Driver> driverService = new DriverService();

        try {

            Location location = new Location(1, "123 Main St", 36.8065f, 10.1815f);
            User newUser = new User(1,
                    "John Doe",
                    "john@example.com",
                    "15678",
                    "securepassword",
                    User.Role.CLIENT,
                    location);
            userService.create(newUser);

            User otherUser = new User(2,
                    "Ahmed Ali",
                    "ahmed@example.com",
                    "123456", "passs",
                    User.Role.CLIENT, location);
            userService.create(otherUser);

            Driver driver = new Driver(
                    1,
                    otherUser.getId(),
                    otherUser.getName(),
                    otherUser.getEmail(),
                    otherUser.getPhone(),
                    otherUser.getPassword(),
                    Driver.DriverRole.TAXI_DRIVER,
                    otherUser.getLocation(),
                    "111",
                    1
            );
            driverService.create(driver);

            System.out.println("User and Driver added successfully!");

            System.out.println("All users:");
            userService.read().forEach(System.out::println);

            System.out.println("All drivers:");
            driverService.read().forEach(System.out::println);

            newUser.setName("John Updated");
            userService.update(newUser);
            System.out.println("User updated!");

            User retrievedUser = userService.getById(newUser.getId());
            System.out.println("Retrieved User: " + retrievedUser);

            driver.setName("Ahmed Updated");
            driverService.update(driver);
            System.out.println("Driver updated!");

            Driver retrievedDriver = driverService.getById(driver.getId());
            System.out.println("Retrieved Driver: " + retrievedDriver);

            userService.delete(newUser.getId());
            driverService.delete(driver.getId());
            System.out.println("User and Driver deleted!");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


