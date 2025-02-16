package test;

import entities.*;
import services.AnnouncementService;
import services.ReservationService;
import services.RelocationService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Initialisation des services
        AnnouncementService announcementService = new AnnouncementService();
        ReservationService reservationService = new ReservationService();
        RelocationService relocationService = new RelocationService();

        try {
            // ===========================
            //  Gestion des Annonces (Announcements)
            // ===========================

            //  Ajout d'une nouvelle annonce
            Announcement newAnnouncement = new Announcement();
            newAnnouncement.setIdAnnouncement(1);
            newAnnouncement.setTitle(" pidev");
            newAnnouncement.setContent("We have added a new route to the app!");
            newAnnouncement.setDate(Timestamp.valueOf(LocalDateTime.now()));
            newAnnouncement.setZone(Announcement.Zone.Kairouan);
            newAnnouncement.setStatus(true);

            newAnnouncement.setTransporter(new Driver());
            newAnnouncement.getTransporter().setIdDriver(1);

            announcementService.create(newAnnouncement);
//            System.out.println(" Announcement added successfully!");

            //  Récupération et affichage de toutes les annonces
            List<Announcement> announcements = announcementService.read();
            System.out.println(" All Announcements:");
            for (Announcement announcement : announcements) {
                System.out.println(announcement);

            }

            //  Mise à jour
            if (!announcements.isEmpty()) {
                // Récupérer la première annonce de la liste
                Announcement firstAnnouncement = announcements.get(0);

                // Modifier les propriétés de l'annonce
                firstAnnouncement.setTransporter(new Driver());
                firstAnnouncement.getTransporter().setIdDriver(1);
                firstAnnouncement.setTitle("Updated");
                firstAnnouncement.setContent("This route has been updated.");
                firstAnnouncement.setStatus(true);

                // Mettre à jour l'annonce dans la base de données
                announcementService.update(firstAnnouncement);

                // Afficher l'annonce mise à jour
                System.out.println("Updated Announcement: " + firstAnnouncement);
            } else {
                System.out.println("No announcements available to update.");
            }

            //  Suppression de la dernière annonce (si disponible)
            if (!announcements.isEmpty()) {
                int announcementIdToDelete = announcements.get(announcements.size() - 1).getIdAnnouncement();
                announcementService.delete(announcementIdToDelete);
                System.out.println(" Announcement with ID " + announcementIdToDelete + " deleted successfully!");
            } else {
                System.out.println(" No announcements available to delete.");
            }

            // ===========================
            //  Gestion des Réservations (Reservations)
            // ===========================

            //  Ajout d'une nouvelle réservation
            Reservation newReservation = new Reservation();
            newReservation.setDate(Timestamp.valueOf(LocalDateTime.now()));
            newReservation.setStatus(Reservation.Status.CONFIRMED);
            newReservation.setDescription(" pidev");

            newReservation.setStartLocation(new Location());
            newReservation.getStartLocation().setId(1);

            newReservation.setEndLocation(new Location());
            newReservation.getEndLocation().setId(1);

            newReservation.setAnnouncement(new Announcement());
            newReservation.getAnnouncement().setIdAnnouncement(26);

            reservationService.create(newReservation);
            //System.out.println(" Reservation added successfully!");

            //  Récupération et affichage de toutes les réservations
            List<Reservation> reservations = reservationService.read();
            System.out.println(" All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }

            // Mise à jour de la première réservation
            if (!reservations.isEmpty()) {
                Reservation firstReservation = reservations.get(0);
                firstReservation.setStatus(Reservation.Status.ON_GOING);
                firstReservation.setDescription("This reservation is updated.....");
                reservationService.update(firstReservation);
                //System.out.println(" Reservation updated successfully!");

                // Affichage de la réservation mise à jour
                System.out.println(" Updated Reservation: " + firstReservation);
            } else {
                System.out.println(" No reservations available to update.");
            }

            //  Suppression de la dernière réservation (si disponible)
            if (!reservations.isEmpty()) {
                int reservationIdToDelete = reservations.get(reservations.size() - 1).getIdReservation();
                reservationService.delete(reservationIdToDelete);
                System.out.println(" Reservation with ID " + reservationIdToDelete + " deleted successfully!");
            } else {
                System.out.println(" No reservations available to delete.");
            }

            // ===========================
            //  Gestion des Déménagements (Relocations)
            // ===========================

            //  Ajout d'un nouveau déménagement (en utilisant une réservation existante)
            Relocation newRelocation = new Relocation();
            newRelocation.setDate(Timestamp.valueOf(LocalDateTime.now()));
            newRelocation.setStatus(true);
            newRelocation.setCost(200.0f);

            // Utiliser un ID de réservation existant
            newRelocation.setReservation(new Reservation());
            newRelocation.getReservation().setIdReservation(30);

            relocationService.create(newRelocation);
            //System.out.println(" Relocation added successfully!");

            //  Récupération et affichage de tous les déménagements
            List<Relocation> relocations = relocationService.read();
            System.out.println(" All Relocations:");
            for (Relocation relocation : relocations) {
                System.out.println(relocation);
            }

            //  Mise à jour du premier déménagement (si disponible)
            if (!relocations.isEmpty()) {
                Relocation firstRelocation = relocations.get(0);
                firstRelocation.setStatus(false);
                firstRelocation.setCost(192.0f);
                relocationService.update(firstRelocation);
                //System.out.println(" Relocation updated successfully!");

                // Affichage du déménagement mis à jour
                System.out.println(" Updated Relocation: " + firstRelocation);
            } else {
                System.out.println(" No relocations available to update.");
            }

            //  Suppression du dernier déménagement (si disponible)
            if (!relocations.isEmpty()) {
                int relocationIdToDelete = relocations.get(relocations.size() - 1).getIdRelocation();
                relocationService.delete(relocationIdToDelete);
                System.out.println(" Relocation with ID " + relocationIdToDelete + " deleted successfully!");
            } else {
                System.out.println(" No relocations available to delete.");
            }

            // ===========================
            //  Tests de recherche dynamique
            // ===========================
            runDynamicSearchTests(announcementService, reservationService, relocationService);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" An error occurred while performing database operations: " + e.getMessage());
        }
    }
    private static void runDynamicSearchTests(AnnouncementService announcementService,
                                              ReservationService reservationService,
                                              RelocationService relocationService) throws SQLException {
        System.out.println("\n==============================");
        System.out.println(" TESTS DE RECHERCHE DYNAMIQUE");
        System.out.println("==============================\n");

        // Cas 1 : Recherche avec un seul critère (titre)
        Map<String, Object> filters1 = new HashMap<>();
        filters1.put("title", " pidev");
        filters1.put("status", true);
        testSearch(" Recherche d'annonces par titre", announcementService.findByFilters(filters1));

        // Cas 2 : Recherche avec plusieurs critères (titre + zone)
        Map<String, Object> filters2 = new HashMap<>();
        filters2.put("title", " pidev");
        filters2.put("zone", "Ariana");
        testSearch(" Recherche d'annonces par titre et zone", announcementService.findByFilters(filters2));

        // Cas 3 : Recherche avec une date spécifique
        Map<String, Object> filters3 = new HashMap<>();
        filters3.put("date", Timestamp.valueOf("2025-02-14 01:01:03"));
        testSearch(" Recherche d'annonces par date", announcementService.findByFilters(filters3));

        // Cas 4 : Recherche avec plusieurs critères incluant une date
        Map<String, Object> filters4 = new HashMap<>();
        filters4.put("title", "Updated");
        filters4.put("date", Timestamp.valueOf("2025-02-14 00:38:59"));
        testSearch(" Recherche d'annonces par titre et date", announcementService.findByFilters(filters4));

        // Cas 5 : Recherche sans résultat
        Map<String, Object> filters5 = new HashMap<>();
        filters5.put("title", "Inexistant");
        testSearch(" Recherche avec un critère inexistant", announcementService.findByFilters(filters5));

        // Tests pour ReservationService
        System.out.println("\n==============================");
        System.out.println(" TESTS SUR LES RÉSERVATIONS");
        System.out.println("==============================\n");

        Map<String, Object> reservationFilters = new HashMap<>();
        //reservationFilters.put("status", Reservation.Status.CONFIRMED);
        reservationFilters.put("description"," pidev" );
        testSearch(" Recherche de réservations confirmées", reservationService.findByFilters(reservationFilters));

        // Tests pour RelocationService
        System.out.println("\n==============================");
        System.out.println("TESTS SUR LES RELOCATIONS");
        System.out.println("==============================\n");

        Map<String, Object> relocationFilters = new HashMap<>();
        relocationFilters.put("status", true);
        relocationFilters.put("cost", 200.0f);
        testSearch(" Recherche de relocalisations actives", relocationService.findByFilters(relocationFilters));
    }

    private static <T> void testSearch(String testCaseDescription, List<T> results) {
        System.out.println("\n" + testCaseDescription);
        if (results.isEmpty()) {
            System.out.println(" Aucun résultat trouvé.");
        } else {
            System.out.println(" Résultats trouvés :");
            for (T result : results) {
                System.out.println(result);
            }
        }
    }
}