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


        Request newRequest = new Request(
                15,
                11,
                1,
                2,
                Request.RequestStatus.PENDING,
                new Date()
        );

        try {

            //requestService.create(newRequest);
            //System.out.println("Request added successfully.");


            System.out.println("Displaying all requests:");
            List<Request> requests = requestService.displayAllRequests();


            for (Request request : requests) {
                System.out.println("Request ID: " + request.getIdRequest());
                System.out.println("Client ID: " + request.getIdClient());
                System.out.println("Taxi ID: " + request.getIdTaxi());
                System.out.println("Departure Location ID: " + request.getIdDepartureLocation());
                System.out.println("Arrival Location ID: " + request.getIdArrivalLocation());
                System.out.println("Status: " + request.getStatus());
                System.out.println("Request Date: " + request.getRequestDate());
                System.out.println("====================================");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
