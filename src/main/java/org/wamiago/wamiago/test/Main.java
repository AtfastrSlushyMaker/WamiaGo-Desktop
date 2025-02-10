package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.services.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();

        try {

            Location location = new Location(1, "123 Main Street", 36.8065f, 10.1815f);

            User newUser = new User(1, "John Doe", "john@example.com", "12345678", "securepassword", User.Role.ADMIN, location);

            userService.addUser(newUser);
            System.out.println("✅ User added successfully!");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
