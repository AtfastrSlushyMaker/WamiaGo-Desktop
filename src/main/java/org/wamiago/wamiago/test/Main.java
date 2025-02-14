package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.*;
import org.wamiago.wamiago.services.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import javax.swing.*;
import java.io.File;
public class Main {
    private static UserService userService = new UserService();
    private static IService<Driver> driverService = new DriverService();
    private static RatingService ratingService = new RatingService();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                displayMainMenu();
                int choice = promptForIntegerInRange("Choose an option: ", 1, 15, scanner);

                switch (choice) {
                    case 1:
                        createUser(scanner);
                        break;
                    case 2:
                        createDriver(scanner);
                        break;
                    case 3:
                        manageRatings(scanner);
                        break;
                    case 4:
                        viewAllUsers();
                        break;
                    case 5:
                        viewAllDrivers();
                        break;
                    case 6:
                        viewAllRatings();
                        break;
                    case 7:
                        searchUsers(scanner);
                        break;
                    case 8:
                        updateUser(scanner);
                        break;
                    case 9:
                        updateDriver(scanner);
                        break;
                    case 10:
                        deleteUser(scanner);
                        break;
                    case 11:
                        deleteDriver(scanner);
                        break;
                    case 12:
                        sortUsers(scanner);
                        break;
                        case 13:
                            generatePDF();
                            break;
                            case 14:
                                displayRatingsLeaderboard();
                                break;
                            case 15:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" +
                " _       __     __                             ______         _       __                _       ______    \n" +
                "| |     / /__  / /________  ____ ___  ___     /_  __/___     | |     / /___ _____ ___  (_)___ _/ ____/___ \n" +
                "| | /| / / _ \\/ / ___/ __ \\/ __ `__ \\/ _ \\     / / / __ \\    | | /| / / __ `/ __ `__ \\/ / __ `/ / __/ __ \\\n" +
                "| |/ |/ /  __/ / /__/ /_/ / / / / / /  __/    / / / /_/ /    | |/ |/ / /_/ / / / / / / / /_/ / /_/ / /_/ /\n" +
                "|__/|__/\\___/_/\\___/\\____/_/ /_/ /_/\\___/    /_/  \\____/     |__/|__/\\__,_/_/ /_/ /_/_/\\__,_/\\____/\\____/ \n" +
                "                                                                                                          \n");
        System.out.println("\n### Main Menu ###");
        System.out.println("1. Create User");
        System.out.println("2. Convert User to Driver");
        System.out.println("3. Manage Ratings");
        System.out.println("4. View All Users");
        System.out.println("5. View All Drivers");
        System.out.println("6. View All Ratings");
        System.out.println("7. Search Users");
        System.out.println("8. Update User");
        System.out.println("9. Update Driver");
        System.out.println("10. Delete User");
        System.out.println("11. Delete Driver");
        System.out.println("12. Sort Users");
        System.out.println("13. Generate A PDF report for Users and Drivers.");
        System.out.println("14. Show Leaderboard Stats for Drivers.");
        System.out.println("15. Exit");
    }
    //CRUD
    //-----------------------------------------------------------
    //USER
    private static void createUser(Scanner scanner) throws SQLException {
        System.out.println("\n### Create User ###");

        int id = promptForPositiveInteger("Enter ID: ", "Invalid ID. ID must be a positive integer.", scanner);
        String name = promptForNonEmptyString("Enter name: ", "Name cannot be empty.", scanner);
        String email = promptForValidInput("Enter email: ", Main::isValidEmail, "Invalid email format.", scanner);
        String phone = promptForValidInput("Enter phone: ", Main::isValidPhone, "Invalid phone number.", scanner);
        String password = promptForValidInput("Enter password: ", p -> p.length() >= 8, "Password must be at least 8 characters long.", scanner);

        User.Role role = selectEnum(User.Role.class, "\n### Select Role ###", scanner);
        User.Gender gender = selectEnum(User.Gender.class, "\n### Select Gender ###", scanner);
        User.AccountStatus accountStatus = selectEnum(User.AccountStatus.class, "\n### Select Account Status ###", scanner);
        User.Status status = selectEnum(User.Status.class, "\n### Select User Status ###", scanner);

        LocalDate dateOfBirth = promptForDate("Enter date of birth (yyyy-mm-dd): ", "Invalid date format. Please use yyyy-mm-dd.", scanner);

        int locationId = promptForPositiveInteger("Enter location ID: ", "Invalid location ID. ID must be a positive integer.", scanner);
        String address = promptForNonEmptyString("Enter address: ", "Address cannot be empty.", scanner);
        float latitude = promptForFloat("Enter latitude: ", scanner);
        float longitude = promptForFloat("Enter longitude: ", scanner);

        Location location = new Location(locationId, address, latitude, longitude);
        User newUser = new User(id, name, email, phone, password, role, location, gender, "profile.jpg", true, accountStatus, dateOfBirth, status);

        userService.create(newUser);
        System.out.println("User created successfully!");
    }

    private static void updateUser(Scanner scanner) throws SQLException {
        System.out.println("\n### Update User ###");
        User user = selectUserFromList(scanner);
        if (user == null) {
            return;
        }

        while (true) {
            System.out.println("\n### Select Field to Update ###");
            System.out.println("1. Name");
            System.out.println("2. Email");
            System.out.println("3. Phone");
            System.out.println("4. Password");
            System.out.println("5. Role");
            System.out.println("6. Gender");
            System.out.println("7. Profile Picture");
            System.out.println("8. Verification Status");
            System.out.println("9. Account Status");
            System.out.println("10. Date of Birth");
            System.out.println("11. Status");
            System.out.println("12. Location");
            System.out.println("13. Save and Exit");

            int choice = promptForIntegerInRange("Choose an option: ", 1, 13, scanner);

            switch (choice) {
                case 1:
                    user.setName(promptForNonEmptyString("Enter new name: ", "Name cannot be empty.", scanner));
                    break;
                case 2:
                    user.setEmail(promptForValidInput("Enter new email: ", Main::isValidEmail, "Invalid email format.", scanner));
                    break;
                case 3:
                    user.setPhone(promptForValidInput("Enter new phone: ", Main::isValidPhone, "Invalid phone number.", scanner));
                    break;
                case 4:
                    user.setPassword(promptForValidInput("Enter new password: ", p -> p.length() >= 8, "Password must be at least 8 characters long.", scanner));
                    break;
                case 5:
                    user.setRole(selectEnum(User.Role.class, "\n### Select New Role ###", scanner));
                    break;
                case 6:
                    user.setGender(selectEnum(User.Gender.class, "\n### Select New Gender ###", scanner));
                    break;
                case 7:
                    user.setProfilePicture(promptForNonEmptyString("Enter new profile picture filename: ", "Filename cannot be empty.", scanner));
                    break;
                case 8:
                    boolean verificationStatus = promptForIntegerInRange("Is user verified? (0 for NO, 1 for YES): ", 0, 1, scanner) == 1;
                    user.setVerified(verificationStatus);
                    break;
                case 9:
                    user.setAccountStatus(selectEnum(User.AccountStatus.class, "\n### Select New Account Status ###", scanner));
                    break;
                case 10:
                    user.setDateOfBirth(promptForDate("Enter new date of birth (yyyy-mm-dd): ", "Invalid date format. Use yyyy-mm-dd.", scanner));
                    break;
                case 11:
                    user.setStatus(selectEnum(User.Status.class, "\n### Select New Status ###", scanner));
                    break;
                case 12:
                    int locationId = promptForPositiveInteger("Enter new location ID: ", "Invalid location ID. Must be positive.", scanner);
                    String address = promptForNonEmptyString("Enter new address: ", "Address cannot be empty.", scanner);
                    float latitude = promptForFloat("Enter new latitude: ", scanner);
                    float longitude = promptForFloat("Enter new longitude: ", scanner);
                    user.setLocation(new Location(locationId, address, latitude, longitude));
                    break;
                case 13:
                    userService.update(user);
                    System.out.println("User updated successfully!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            userService.update(user);
            System.out.println("Update saved. Choose another option or exit.");
        }
    }

    private static void deleteUser(Scanner scanner) throws SQLException {
        System.out.println("\n### Delete User ###");
        User user = selectUserFromList(scanner);
        if (user == null) {
            return;
        }

        String confirmation = promptForNonEmptyString("Are you sure you want to delete this user? (yes/no): ", "Invalid input.", scanner);
        if (confirmation.equalsIgnoreCase("yes")) {
            userService.delete(user.getId());
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private static void viewAllUsers() throws SQLException {
        System.out.println("\n### All Users ###");
        List<User> users = userService.read();
        users.forEach(System.out::println);
    }

    private static void searchUsers(Scanner scanner) throws SQLException {
        System.out.println("\n### Search Users ###");
        System.out.print("Enter search field (name, email, phone, role, gender, accountStatus, dob, status, location): ");
        String searchField = scanner.nextLine().trim();

        if (!isValidSearchField(searchField)) {
            System.out.println("Invalid search field.");
            return;
        }

        System.out.print("Enter search value: ");
        String searchValue = scanner.nextLine().trim();

        List<User> foundUsers = userService.searchUsers(searchField, searchValue);
        if (foundUsers.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("Search Results:");
            foundUsers.forEach(System.out::println);
        }
    }

    private static void sortUsers(Scanner scanner) throws SQLException {
        System.out.println("\n### Sort Users ###");
        System.out.println("1. Sort by Name");
        System.out.println("2. Sort by Email");
        System.out.println("3. Sort by Date of Birth");
        System.out.println("4. Sort by Role");
        System.out.println("5. Sort by Gender");
        System.out.println("6. Sort by Account Status");
        System.out.println("7. Sort by Status");
        System.out.println("8. Sort by Location");
        int choice = promptForIntegerInRange("Choose an option: ", 1, 8, scanner);

        String sortField;
        switch (choice) {
            case 1:
                sortField = "name";
                break;
            case 2:
                sortField = "email";
                break;
            case 3:
                sortField = "dateOfBirth";
                break;
            case 4:
                sortField = "role";
                break;
            case 5:
                sortField = "gender";
                break;
            case 6:
                sortField = "accountStatus";
                break;
            case 7:
                sortField = "status";
                break;
            case 8:
                sortField = "location";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("\n### Sort Order ###");
        System.out.println("1. Ascending (ASC)");
        System.out.println("2. Descending (DESC)");
        int orderChoice = promptForIntegerInRange("Choose an option: ", 1, 2, scanner);

        boolean ascending = orderChoice == 1;
        List<User> sortedUsers = userService.sortUsers(sortField, ascending);
        System.out.println("\n### Sorted Users ###");
        sortedUsers.forEach(System.out::println);
    }

    private static void generatePDF() throws SQLException {
        System.out.println("\n### Generating PDF Report ###");

        // Create a parent frame to ensure the dialog appears in front
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true); // Force it to appear in front

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a location to save the PDF");

        // Set the default file name
        fileChooser.setSelectedFile(new File("users_drivers_report.pdf"));

        // Show the save dialog and make sure it appears in front
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Ensure the file has a .pdf extension
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            // Call the export function with the selected path
            userService.exportToPdf(filePath);
            System.out.println("PDF successfully generated at: " + filePath);
        } else {
            System.out.println("PDF generation canceled.");
        }

        frame.dispose(); // Close the frame after use
    }
    //DRIVER
    private static void createDriver(Scanner scanner) throws SQLException {
        System.out.println("\n### Convert User to Driver ###");

        List<User> users = userService.read();
        if (users.isEmpty()) {
            System.out.println("No users found. Please create a user first.");
            return;
        }

        System.out.println("\n### Select User to Convert to Driver ###");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " (ID: " + users.get(i).getId() + ")");
        }

        int userChoice = promptForIntegerInRange("Choose a user: ", 1, users.size(), scanner);
        User selectedUser = users.get(userChoice - 1);

        int driverId = promptForPositiveInteger("Enter new Driver ID: ", "Invalid driver ID. Must be a positive integer.", scanner);
        String permitNumber = promptForNonEmptyString("Enter permit number: ", "Permit number cannot be empty.", scanner);
        Driver.DriverRole driverRole = selectEnum(Driver.DriverRole.class, "\n### Select Driver Role ###", scanner);
        int driverStatus = promptForIntegerInRange("Enter driver status (0 for INACTIVE, 1 for ACTIVE): ", 0, 1, scanner);

        Driver newDriver = new Driver(
                driverId, selectedUser.getId(), selectedUser.getName(), selectedUser.getEmail(), selectedUser.getPhone(),
                selectedUser.getPassword(), driverRole, selectedUser.getLocation(), permitNumber, driverStatus,
                selectedUser.getGender(), selectedUser.getProfilePicture(), selectedUser.isVerified(),
                selectedUser.getAccountStatus(), selectedUser.getDateOfBirth(), selectedUser.getStatus()
        );

        driverService.create(newDriver);
        System.out.println("User " + selectedUser.getName() + " is now a Driver!");
    }

    private static void updateDriver(Scanner scanner) throws SQLException {
        System.out.println("\n### Update Driver ###");
        Driver driver = selectDriverFromList(scanner);
        if (driver == null) {
            return;
        }

        while (true) {
            System.out.println("\n### Select Field to Update ###");
            System.out.println("1. Name");
            System.out.println("2. Email");
            System.out.println("3. Phone");
            System.out.println("4. Password");
            System.out.println("5. Driver Role");
            System.out.println("6. Permit Number");
            System.out.println("7. Driver Status");
            System.out.println("8. Gender");
            System.out.println("9. Profile Picture");
            System.out.println("10. Verification Status");
            System.out.println("11. Account Status");
            System.out.println("12. Date of Birth");
            System.out.println("13. Status");
            System.out.println("14. Location");
            System.out.println("15. Save and Exit");

            int choice = promptForIntegerInRange("Choose an option: ", 1, 15, scanner);

            switch (choice) {
                case 1:
                    driver.setName(promptForNonEmptyString("Enter new name: ", "Name cannot be empty.", scanner));
                    break;
                case 2:
                    driver.setEmail(promptForValidInput("Enter new email: ", Main::isValidEmail, "Invalid email format.", scanner));
                    break;
                case 3:
                    driver.setPhone(promptForValidInput("Enter new phone: ", Main::isValidPhone, "Invalid phone number.", scanner));
                    break;
                case 4:
                    driver.setPassword(promptForValidInput("Enter new password: ", p -> p.length() >= 8, "Password must be at least 8 characters long.", scanner));
                    break;
                case 5:
                    driver.setDriverRole(selectEnum(Driver.DriverRole.class, "\n### Select New Driver Role ###", scanner));
                    break;
                case 6:
                    driver.setPermitNumber(promptForNonEmptyString("Enter new permit number: ", "Permit number cannot be empty.", scanner));
                    break;
                case 7:
                    int driverStatus = promptForIntegerInRange("Enter new driver status (0 for INACTIVE, 1 for ACTIVE): ", 0, 1, scanner);
                    driver.setDriverStatus(driverStatus);
                    break;
                case 8:
                    driver.setGender(selectEnum(User.Gender.class, "\n### Select New Gender ###", scanner));
                    break;
                case 9:
                    driver.setProfilePicture(promptForNonEmptyString("Enter new profile picture filename: ", "Filename cannot be empty.", scanner));
                    break;
                case 10:
                    boolean verificationStatus = promptForIntegerInRange("Is driver verified? (0 for NO, 1 for YES): ", 0, 1, scanner) == 1;
                    driver.setVerified(verificationStatus);
                    break;
                case 11:
                    driver.setAccountStatus(selectEnum(User.AccountStatus.class, "\n### Select New Account Status ###", scanner));
                    break;
                case 12:
                    driver.setDateOfBirth(promptForDate("Enter new date of birth (yyyy-mm-dd): ", "Invalid date format. Use yyyy-mm-dd.", scanner));
                    break;
                case 13:
                    driver.setStatus(selectEnum(User.Status.class, "\n### Select New Status ###", scanner));
                    break;
                case 14:
                    int locationId = promptForPositiveInteger("Enter new location ID: ", "Invalid location ID. Must be positive.", scanner);
                    String address = promptForNonEmptyString("Enter new address: ", "Address cannot be empty.", scanner);
                    float latitude = promptForFloat("Enter new latitude: ", scanner);
                    float longitude = promptForFloat("Enter new longitude: ", scanner);
                    driver.setLocation(new Location(locationId, address, latitude, longitude));
                    break;
                case 15:
                    driverService.update(driver);
                    System.out.println("Driver updated successfully!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

            driverService.update(driver);
            System.out.println("Update saved. Choose another option or exit.");
        }
    }

    private static void deleteDriver(Scanner scanner) throws SQLException {
        System.out.println("\n### Delete Driver ###");
        Driver driver = selectDriverFromList(scanner);
        if (driver == null) {
            return;
        }

        String confirmation = promptForNonEmptyString("Are you sure you want to delete this driver? (yes/no): ", "Invalid input.", scanner);
        if (confirmation.equalsIgnoreCase("yes")) {
            driverService.delete(driver.getIdDriver());
            System.out.println("Driver deleted successfully!");
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private static void viewAllDrivers() throws SQLException {
        System.out.println("\n### All Drivers ###");
        List<Driver> drivers = driverService.read();
        drivers.forEach(System.out::println);
    }

    private static void displayRatingsLeaderboard() throws SQLException {
        System.out.println("\n### Top 5 Drivers Leaderboard ###");
        List<Object[]> leaderboard = ratingService.getTopRatedDrivers(5);

        if (leaderboard.isEmpty()) {
            System.out.println("No ratings available yet.");
            return;
        }

        System.out.printf("%-10s %-20s %-10s %-10s%n", "Driver ID", "Name", "Avg Rating", "Total Ratings");
        System.out.println("------------------------------------------------------");

        for (Object[] driver : leaderboard) {
            System.out.printf("%-10d %-20s %-10.2f %-10d%n",
                    driver[0], driver[1], driver[2], driver[3]);
        }
    }


    //RATING
    private static void addRating(Scanner scanner) throws SQLException {
        System.out.println("\n### Add Rating ###");

        User user = selectUserFromList(scanner);
        if (user == null) {
            return;
        }

        Driver driver = selectDriverFromList(scanner);
        if (driver == null) {
            return;
        }

        String comment = promptForNonEmptyString("Enter comment: ", "Comment cannot be empty.", scanner);
        int ratingValue = promptForIntegerInRange("Enter rating (1-5): ", 1, 5, scanner);

        Rating newRating = new Rating(0, user.getId(), driver.getIdDriver(), comment, ratingValue);
        ratingService.create(newRating);
        System.out.println("Rating added successfully!");
    }

    private static void updateRating(Scanner scanner) throws SQLException {
        System.out.println("\n### Update Rating ###");
        Rating rating = selectRatingFromList(scanner);
        if (rating == null) {
            return;
        }

        while (true) {
            System.out.println("\n### Select Field to Update ###");
            System.out.println("1. Comment");
            System.out.println("2. Rating Value");
            System.out.println("3. Save and Exit");
            int choice = promptForIntegerInRange("Choose an option: ", 1, 3, scanner);

            switch (choice) {
                case 1:
                    rating.setComment(promptForNonEmptyString("Enter new comment: ", "Comment cannot be empty.", scanner));
                    break;
                case 2:
                    rating.setRating(promptForIntegerInRange("Enter new rating (1-5): ", 1, 5, scanner));
                    break;
                case 3:
                    ratingService.update(rating);
                    System.out.println("Rating updated successfully!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void deleteRating(Scanner scanner) throws SQLException {
        System.out.println("\n### Delete Rating ###");
        Rating rating = selectRatingFromList(scanner);
        if (rating == null) {
            return;
        }

        String confirmation = promptForNonEmptyString("Are you sure you want to delete this rating? (yes/no): ", "Invalid input.", scanner);
        if (confirmation.equalsIgnoreCase("yes")) {
            ratingService.delete(rating.getIdRating());
            System.out.println("Rating deleted successfully!");
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private static void viewAllRatings() throws SQLException {
        System.out.println("\n### All Ratings ###");
        List<Rating> ratings = ratingService.read();
        ratings.forEach(System.out::println);
    }

    private static void manageRatings(Scanner scanner) throws SQLException {
        System.out.println("\n### Manage Ratings ###");
        System.out.println("1. Add Rating");
        System.out.println("2. Update Rating");
        System.out.println("3. Delete Rating");
        System.out.println("4. Back to Main Menu");
        int choice = promptForIntegerInRange("Choose an option: ", 1, 4, scanner);

        switch (choice) {
            case 1:
                addRating(scanner);
                break;
            case 2:
                updateRating(scanner);
                break;
            case 3:
                deleteRating(scanner);
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    //-----------------------------------------------------------

    //HELPER METHODS

    private static User selectUserFromList(Scanner scanner) throws SQLException {
        List<User> users = userService.read();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return null;
        }

        System.out.println("\n### Select User ###");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " (ID: " + users.get(i).getId() + ")");
        }

        int choice = promptForIntegerInRange("Choose an option: ", 1, users.size(), scanner);
        return users.get(choice - 1);
    }

    private static Rating selectRatingFromList(Scanner scanner) throws SQLException {
        List<Rating> ratings = ratingService.read();
        if (ratings.isEmpty()) {
            System.out.println("No ratings found.");
            return null;
        }

        System.out.println("\n### Select Rating ###");
        for (int i = 0; i < ratings.size(); i++) {
            System.out.println((i + 1) + ". Rating ID: " + ratings.get(i).getIdRating() + " - User ID: " + ratings.get(i).getUserId() + " - Driver ID: " + ratings.get(i).getDriverId());
        }

        int choice = promptForIntegerInRange("Choose an option: ", 1, ratings.size(), scanner);
        return ratings.get(choice - 1);
    }

    private static int promptForPositiveInteger(String prompt, String errorMessage, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            int value = scanner.nextInt();
            scanner.nextLine();
            if (value > 0) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }

    private static String promptForNonEmptyString(String prompt, String errorMessage, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input != null && !input.trim().isEmpty()) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    private static <T extends Enum<T>> T selectEnum(Class<T> enumClass, String prompt, Scanner scanner) {
        T[] enumValues = enumClass.getEnumConstants();
        while (true) {
            System.out.println(prompt);
            for (int i = 0; i < enumValues.length; i++) {
                System.out.println((i + 1) + ". " + enumValues[i]);
            }
            int choice = promptForIntegerInRange("Choose an option: ", 1, enumValues.length, scanner);
            return enumValues[choice - 1];
        }
    }

    private static int promptForIntegerInRange(String prompt, int min, int max, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice >= min && choice <= max) {
                return choice;
            }
            System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }

    private static String promptForValidInput(String prompt, Predicate<String> validator, String errorMessage, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    private static LocalDate promptForDate(String prompt, String errorMessage, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println(errorMessage);
            }
        }
    }

    private static float promptForFloat(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            try {
                return Float.parseFloat(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static Driver selectDriverFromList(Scanner scanner) throws SQLException {
        List<Driver> drivers = driverService.read();
        if (drivers.isEmpty()) {
            System.out.println("No drivers found.");
            return null;
        }

        System.out.println("\n### Select Driver ###");
        for (int i = 0; i < drivers.size(); i++) {
            System.out.println((i + 1) + ". " + drivers.get(i).getName() + " (ID: " + drivers.get(i).getIdDriver() + ")");
        }

        int choice = promptForIntegerInRange("Choose an option: ", 1, drivers.size(), scanner);
        return drivers.get(choice - 1);
    }

    private static boolean isValidSearchField(String field) {
        return field.equals("name") || field.equals("email") || field.equals("phone") ||
                field.equals("role") || field.equals("gender") || field.equals("accountStatus") ||
                field.equals("dob") || field.equals("status") || field.equals("location");
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-z0-9+_.-]+@(.+)$");
    }

    private static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{8}$");
    }

}