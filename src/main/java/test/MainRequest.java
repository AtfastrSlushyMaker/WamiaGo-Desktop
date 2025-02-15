package test;

import entities.Request;
import entities.User;
import entities.Driver;
import entities.Location;
import services.RequestService;
import services.UserService;  // Make sure to import UserService
import services.DriverService;  // Make sure to import DriverService
import services.LocationService;  // Make sure to import LocationService
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainRequest {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        RequestService requestService = new RequestService();
        UserService userService = new UserService();  // Instantiate UserService
        DriverService driverService = new DriverService();  // Instantiate DriverService
        LocationService locationService = new LocationService();  // Instantiate LocationService

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Create Request");
            System.out.println("2. Read Request");
            System.out.println("3. Update Request");
            System.out.println("4. Delete Request");
            System.out.println("5. List all Requests");
            System.out.println("6. Exit");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createRequest(requestService, userService, driverService, locationService, scanner);
                    break;
                case 2:
                    readRequest(requestService, scanner);
                    break;
                case 3:
                    updateRequest(requestService, scanner);
                    break;
                case 4:
                    deleteRequest(requestService, scanner);
                    break;
                case 5:
                    listAllRequests(requestService);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void createRequest(RequestService requestService, UserService userService, DriverService driverService, LocationService locationService, Scanner scanner) throws SQLException {
        // Sample input, modify with actual request fields
        System.out.print("Enter client ID: ");
        int clientId = scanner.nextInt();
        System.out.print("Enter driver ID: ");
        int driverId = scanner.nextInt();
        System.out.print("Enter departure location ID: ");
        int departureId = scanner.nextInt();
        System.out.print("Enter arrival location ID: ");
        int arrivalId = scanner.nextInt();
        System.out.print("Enter request status (PENDING, ACCEPTED, etc.): ");
        String status = scanner.next();

        User client = userService.getById(clientId);  // Use UserService
        Driver driver = driverService.getById(driverId);  // Use DriverService
        Location departure = locationService.getById(departureId);  // Use LocationService
        Location arrival = locationService.getById(arrivalId);  // Use LocationService

        Request newRequest = new Request(client, driver, departure, arrival, Request.RequestStatus.valueOf(status), null);

        requestService.create(newRequest);
        System.out.println("Request created successfully!");
    }

    private static void readRequest(RequestService requestService, Scanner scanner) throws SQLException {
        System.out.print("Enter request ID to read: ");
        int requestId = scanner.nextInt();
        Request request = requestService.getById(requestId);
        if (request != null) {
            System.out.println("Request Details: " + request);
        } else {
            System.out.println("Request not found!");
        }
    }

    private static void updateRequest(RequestService requestService, Scanner scanner) throws SQLException {
        System.out.print("Enter request ID to update: ");
        int requestId = scanner.nextInt();
        Request request = requestService.getById(requestId);
        if (request != null) {
            // Update fields as required
            System.out.print("Enter new status (PENDING, ACCEPTED, etc.): ");
            String status = scanner.next();
            request.setStatus(Request.RequestStatus.valueOf(status));
            requestService.update(request);
            System.out.println("Request updated successfully!");
        } else {
            System.out.println("Request not found!");
        }
    }

    private static void deleteRequest(RequestService requestService, Scanner scanner) throws SQLException {
        System.out.print("Enter request ID to delete: ");
        int requestId = scanner.nextInt();
        requestService.delete(requestId);
        System.out.println("Request deleted successfully!");
    }

    private static void listAllRequests(RequestService requestService) throws SQLException {
        List<Request> requests = requestService.read();
        if (requests.isEmpty()) {
            System.out.println("No requests found.");
        } else {
            requests.forEach(System.out::println);
        }
    }
}
