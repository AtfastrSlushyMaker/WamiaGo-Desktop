package org.wamiago.wamiago.utils;

import org.wamiago.wamiago.entities.Request;
import org.wamiago.wamiago.services.RequestService;
import java.sql.SQLException;
import java.util.Date;

public class MainRequest {
    public static void main(String[] args) {
        RequestService requestService = new RequestService();

        try {

            Request newRequest = new Request();
            newRequest.setIdClient(2);
            newRequest.setIdTaxi(4);
            newRequest.setIdDepartureLocation(2);
            newRequest.setIdArrivalLocation(3);
            newRequest.setStatus(Request.RequestStatus.PENDING);
            newRequest.setRequestDate(new Date());

            requestService.create(newRequest);
            System.out.println("Request created successfully!");


            int searchId = 4;
            Request foundRequest = requestService.search(searchId);
            if (foundRequest != null) {
                System.out.println("Requête trouvée : " + foundRequest);
            } else {
                System.out.println("Aucune requête trouvée avec l'identifiant " + searchId);
            }


            /*

            List<Request> requests = requestService.read();
            System.out.println("List of all requests:");
            for (Request request : requests) {
                System.out.println(request);
            }


            if (!requests.isEmpty()) {
                Request requestToUpdate = requests.get(0);
                requestToUpdate.setStatus(Request.RequestStatus.ACCEPTED);
                requestService.update(requestToUpdate);
                System.out.println("Request updated successfully!");
            }


            if (!requests.isEmpty()) {
                int requestIdToDelete = requests.get(0).getIdRequest();
                requestService.delete(requestIdToDelete);
                System.out.println("Request deleted successfully!");
            }
            */

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
