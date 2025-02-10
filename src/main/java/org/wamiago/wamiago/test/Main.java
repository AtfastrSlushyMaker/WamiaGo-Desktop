package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.entities.Role;
import org.wamiago.wamiago.services.UserService;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();

        try {
            // 1️⃣ ADD a new user
            User newUser = new User(0, "John Doe", "john@example.com", "12345678", "securepass", Role.CLIENT, 1);
            userService.addUser(newUser);
            System.out.println("✅ User added successfully!");

            // 2️⃣ FETCH all users
            List<User> users = userService.getAllUsers();
            System.out.println("📜 All Users:");
            for (User user : users) {
                System.out.println(user);
            }

            // 3️⃣ UPDATE the first user (if exists)
            if (!users.isEmpty()) {
                User firstUser = users.get(0);
                firstUser.setName("Updated Name");
                firstUser.setPhone("98765432");
                userService.updateUser(firstUser);
                System.out.println("🔄 User updated successfully!");

                // Fetch and print updated user
                User updatedUser = userService.getUserById(firstUser.getId());
                System.out.println("📝 Updated User: " + updatedUser);
            }

            // 4️⃣ DELETE a user by ID
            if (!users.isEmpty()) {
                int userIdToDelete = users.get(users.size() - 1).getId();
                userService.deleteUser(userIdToDelete);
                System.out.println("🗑️ User with ID " + userIdToDelete + " deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
