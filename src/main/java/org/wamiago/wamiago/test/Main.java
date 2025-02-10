package org.wamiago.wamiago.test;

import org.wamiago.wamiago.services.RequestService;
import org.wamiago.wamiago.utils.DataBase;
import entities.Request;
import java.sql.SQLException;
import java.util.Date;

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
            requestService.create(newRequest);
            System.out.println("Request added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
