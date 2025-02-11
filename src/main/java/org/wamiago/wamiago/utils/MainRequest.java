package org.wamiago.wamiago.utils;

import org.wamiago.wamiago.entities.Request;
import org.wamiago.wamiago.services.RequestService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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


            Request anotherRequest = new Request();
            anotherRequest.setIdClient(5);
            anotherRequest.setIdTaxi(5);
            anotherRequest.setIdDepartureLocation(4);
            anotherRequest.setIdArrivalLocation(6);
            anotherRequest.setStatus(Request.RequestStatus.PENDING);
            anotherRequest.setRequestDate(new Date());

            requestService.create(anotherRequest);
            System.out.println("Another request created successfully!");


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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
