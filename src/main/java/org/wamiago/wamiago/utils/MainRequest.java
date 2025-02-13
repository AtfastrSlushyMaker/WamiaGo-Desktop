package org.wamiago.wamiago.utils;

import org.wamiago.wamiago.entities.Request;
import org.wamiago.wamiago.services.RequestService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class MainRequest {
    public static void main(String[] args) {
        RequestService requestService = new RequestService();

        try {
            // === CREATE ===
            Request newRequest = new Request();
            newRequest.setIdClient(2);
            newRequest.setIdTaxi(4);
            newRequest.setIdDepartureLocation(2);
            newRequest.setIdArrivalLocation(3);
            newRequest.setStatus(Request.RequestStatus.PENDING);
            newRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));

            requestService.create(newRequest);
            System.out.println("✅ Request created successfully!");

            // === READ ===
            List<Request> requests = requestService.read();
            System.out.println("\n📋 List of all requests:");
            for (Request request : requests) {
                System.out.println(request);
            }

            // Vérifier si la liste contient des requêtes avant de continuer
            if (!requests.isEmpty()) {
                // === SEARCH ===
                int searchId = requests.get(0).getIdRequest(); // Prendre le premier ID pour tester
                Request foundRequest = requestService.search(searchId);
                if (foundRequest != null) {
                    System.out.println("\n🔍 Request found:");
                    System.out.println(foundRequest);
                } else {
                    System.out.println("\n❌ Request not found.");
                }

                // === UPDATE ===
                Request requestToUpdate = requests.get(0);
                requestToUpdate.setStatus(Request.RequestStatus.ACCEPTED);
                requestService.update(requestToUpdate);
                System.out.println("\n✏️ Request updated successfully!");

                // === DELETE ===
                int requestIdToDelete = requests.get(0).getIdRequest();
                requestService.delete(requestIdToDelete);
                System.out.println("\n🗑️ Request deleted successfully!");
            } else {
                System.out.println("\n⚠️ No requests found for update and delete tests.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
