package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.*;
import org.wamiago.wamiago.services.AnnouncementService;
import org.wamiago.wamiago.services.ReservationService;
import org.wamiago.wamiago.services.RelocationService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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

            //  Ajout d'une nouvelle annonce (en utilisant un transporteur existant)
            Announcement newAnnouncement = new Announcement();
            newAnnouncement.setIdAnnouncement(1); // Utiliser un ID existant ou laisser la base de données l'auto-générer
            newAnnouncement.setTitle(" Route");
            newAnnouncement.setContent("We have added a new route to the app!");
            newAnnouncement.setDate(Timestamp.valueOf(LocalDateTime.now()));
            newAnnouncement.setZone(Announcement.Zone.Kef);
            newAnnouncement.setStatus(true);

            // Utiliser un ID de transporteur existant
            newAnnouncement.setTransporter(new Driver());
            newAnnouncement.getTransporter().setIdDriver(1);

            announcementService.create(newAnnouncement);
            System.out.println(" Announcement added successfully!");

            //  Récupération et affichage de toutes les annonces
            List<Announcement> announcements = announcementService.read();
            System.out.println(" All Announcements:");
            for (Announcement announcement : announcements) {
                System.out.println(announcement);

            }

            //  Mise à jour de la première annonce (si disponible)
            if (!announcements.isEmpty()) {
                Announcement firstAnnouncement = announcements.get(0);
                firstAnnouncement.setTransporter(new Driver());
                firstAnnouncement.getTransporter().getIdDriver();
                firstAnnouncement.setTitle("Updated Route");
                firstAnnouncement.setContent("This route has been updated.");
                announcementService.update(firstAnnouncement);
                System.out.println(" Announcement updated successfully!");

                // Affichage de l'annonce mise à jour
                System.out.println(" Updated Announcement: " + firstAnnouncement);
            } else {
                System.out.println(" No announcements available to update.");
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

            //  Ajout d'une nouvelle réservation (en utilisant des locations et une annonce existantes)
            Reservation newReservation = new Reservation();
            newReservation.setDate(Timestamp.valueOf(LocalDateTime.now()));
            newReservation.setStatus(Reservation.Status.CONFIRMED);
            newReservation.setDescription("First reservation");

            // Utiliser des IDs de locations et d'annonce existants
            newReservation.setStartLocation(new Location());
            newReservation.getStartLocation().setId(1);

            newReservation.setEndLocation(new Location());
            newReservation.getEndLocation().setId(1);

            newReservation.setAnnouncement(new Announcement());
            newReservation.getAnnouncement().setIdAnnouncement(4);

            reservationService.create(newReservation);
            System.out.println(" Reservation added successfully!");

            //  Récupération et affichage de toutes les réservations
            List<Reservation> reservations = reservationService.read();
            System.out.println(" All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }

            // Mise à jour de la première réservation (si disponible)
            if (!reservations.isEmpty()) {
                Reservation firstReservation = reservations.get(0);
                firstReservation.setStatus(Reservation.Status.ON_GOING);
                firstReservation.setDescription("This reservation is updated.");
                reservationService.update(firstReservation);
                System.out.println(" Reservation updated successfully!");

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
            newRelocation.getReservation().setIdReservation(11);

            relocationService.create(newRelocation);
            System.out.println(" Relocation added successfully!");

            //  Récupération et affichage de tous les déménagements
            List<Relocation> relocations = relocationService.read();
            System.out.println("📜 All Relocations:");
            for (Relocation relocation : relocations) {
                System.out.println(relocation);
            }

            //  Mise à jour du premier déménagement (si disponible)
            if (!relocations.isEmpty()) {
                Relocation firstRelocation = relocations.get(0);
                firstRelocation.setStatus(false);
                firstRelocation.setCost(1920.0f);
                relocationService.update(firstRelocation);
                System.out.println(" Relocation updated successfully!");

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

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(" An error occurred while performing database operations: " + e.getMessage());
        }
    }
}