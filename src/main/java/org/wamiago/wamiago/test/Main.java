package org.wamiago.wamiago.test;

import org.wamiago.wamiago.services.RequestService;
import org.wamiago.wamiago.utils.DataBase;
import entities.Request;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        DataBase.getInstance();
        RequestService requestService = new RequestService();

        try {

            Request request1 = new Request(30, 27, 1, 2, Request.RequestStatus.PENDING, new Date());
            Request request2 = new Request(31, 28, 3, 4, Request.RequestStatus.PENDING, new Date());


            requestService.create(request1);
            requestService.create(request2);
            System.out.println("Requests added successfully.\n");


            System.out.println("Requests before update:");
            requestService.displayAllRequests();


            List<Request> requests = requestService.displayAllRequests();
            if (!requests.isEmpty()) {
                Request firstRequest = requests.get(0);
                Request secondRequest = requests.size() > 1 ? requests.get(1) : null;
                Request thirdRequest = requests.size() > 2 ? requests.get(2) : null;

                firstRequest.setStatus(Request.RequestStatus.ACCEPTED);
                firstRequest.setRequestDate(new Date());
                requestService.update(firstRequest);

                if (secondRequest != null) {
                    secondRequest.setStatus(Request.RequestStatus.CANCELED);
                    secondRequest.setRequestDate(new Date());
                    requestService.update(secondRequest);
                }

                if (thirdRequest != null) {
                    thirdRequest.setStatus(Request.RequestStatus.CANCELED);
                    thirdRequest.setRequestDate(new Date());
                    requestService.update(thirdRequest);
                }

                System.out.println("\nRequests updated successfully.\n");


                System.out.println("Requests after update:");
                requestService.displayAllRequests();
            } else {
                System.out.println("No requests found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
