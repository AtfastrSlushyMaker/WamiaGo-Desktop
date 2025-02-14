package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Reclamation;
import org.wamiago.wamiago.entities.Response;
import org.wamiago.wamiago.entities.User;
import org.wamiago.wamiago.services.ReclamationService;
import org.wamiago.wamiago.services.ResponseService;
import org.wamiago.wamiago.services.UserService;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataBase.getInstance().getConnection();


        ReclamationService reclamationService = new ReclamationService();
        ResponseService responseService = new ResponseService();
        UserService userService = new UserService();
        User user = userService.getById(2);

        try {
            // ##### CREATE RECLAMATION #####
            System.out.println("\n##### CREATE RECLAMATION #####");
            Reclamation newReclamation = new Reclamation(user, "Test reclamation content", new Timestamp(System.currentTimeMillis()), 1);
            reclamationService.create(newReclamation);

            Reclamation anotherReclamation = new Reclamation(user, "Another reclamation content", new Timestamp(System.currentTimeMillis()), 2);
            reclamationService.create(anotherReclamation);

            // ##### READ LATEST RECLAMATION #####
            System.out.println("\n##### READ LATEST RECLAMATION #####");
            List<Reclamation> reclamations = reclamationService.read();
            if (reclamations.isEmpty()) {
                System.out.println("❌ No reclamations found.");
                return;
            }
            Reclamation latestReclamation = reclamations.get(reclamations.size() - 1);

            System.out.println("\n📋 All Reclamations:");
            for (Reclamation r : reclamations) {
                System.out.println("📌 " + r.getIdReclamation() + " - " + r.getContent());
            }


            // ##### CREATE RESPONSE #####
            System.out.println("\n##### CREATE RESPONSE #####");
            Response response = new Response(latestReclamation, "This is a response to the reclamation.", new Timestamp(System.currentTimeMillis()));
            responseService.create(response);

            // ##### READ ALL RESPONSES #####
            System.out.println("\n##### READ ALL RESPONSES #####");
            List<Response> responses = responseService.read();
            if (responses.isEmpty()) {
                System.out.println("❌ No responses found.");
            } else {
                System.out.println("\n📋 All Responses:");
                for (Response res : responses) {
                    System.out.println("💬 Reclamation ID: " + res.getReclamation().getIdReclamation() + " - Response: " + res.getContent());
                }
            }

            // ##### UPDATE RESPONSE #####
            System.out.println("\n##### UPDATE RESPONSE #####");
            response.setContent("Updated response colocationntent.");
            responseService.update(response);
            System.out.println("✅ Response updated: " + response.getContent());

            // ##### DELETE RESPONSE #####
            System.out.println("\n##### DELETE RESPONSE #####");
            System.out.println("🆔 Deleting response with ID: " + response.getId_response());
            responseService.delete(response.getId_response());
            System.out.println("✅ Response deleted.");
            // ##### DELETE RECLAMATION #####
            System.out.println("\n##### DELETE RECLAMATION #####");
            System.out.println("🆔 Deleting reclamation with ID: " + anotherReclamation.getIdReclamation());
            reclamationService.delete(anotherReclamation.getIdReclamation());
            System.out.println("✅ Reclamation deleted.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
