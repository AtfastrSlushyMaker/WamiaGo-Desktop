package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.services.UserService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();

        try {

            Location location = new Location(1, "123 Main St", 36.8065f, 10.1815f);


            User newUser = new User(0, "John Doe", "john@example.com", "12345678", "securepassword", User.Role.CLIENT, location);
            User otherUser=new User(1,"Ahmed","Ali","12121212","passs",User.Role.CLIENT,location);
            userService.addUser(newUser);
            userService.addUser(otherUser);
            System.out.println("User added successfully!");


            System.out.println("All users:");
            userService.getAllUsers().forEach(System.out::println);


            newUser.setName("John Updated");
            userService.updateUser(newUser);
            System.out.println("User updated!");


            User retrievedUser = userService.getUserById(12);
            System.out.println("Retrieved User: " + retrievedUser);


            userService.deleteUser(newUser.getId());
            System.out.println("User deleted!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
