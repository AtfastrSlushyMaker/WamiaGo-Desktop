package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Announcement;
import org.wamiago.wamiago.services.AnnouncementService;
import org.wamiago.wamiago.entities.Reservation;
import org.wamiago.wamiago.services.ReservationService;
import org.wamiago.wamiago.entities.Relocation;
import org.wamiago.wamiago.services.RelocationService;

import java.sql.SQLException;
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
            // 🚀 Gestion des Annonces (Announcements)
            // ===========================

            // 1️⃣ Ajout de nouvelles annonces
            Announcement newAnnouncement = new Announcement(0, 1, "New Route", "We have added a new route to the app!", LocalDateTime.now(), Announcement.Zone.Tunis, true);
            Announcement newAnnouncement2 = new Announcement(1, 1, "New Route", "Non Se Compara!", LocalDateTime.now(), Announcement.Zone.Kairouan, false);

            announcementService.create(newAnnouncement);
            announcementService.create(newAnnouncement2);
            System.out.println("✅ Announcements added successfully!");

            // 2️⃣ Récupération et affichage de toutes les annonces
            List<Announcement> announcements = announcementService.read();
            System.out.println("📜 All Announcements:");
            for (Announcement announcement : announcements) {
                System.out.println(announcement);
            }

            // 3️⃣ Mise à jour de la première annonce (si disponible)
            if (!announcements.isEmpty()) {
                Announcement firstAnnouncement = announcements.get(0);
                firstAnnouncement.setTitle("Updated Route CA");
                firstAnnouncement.setContent("This route has been updated.");
                firstAnnouncement.setDate(LocalDateTime.now());
                firstAnnouncement.setStatus(true);
                announcementService.update(firstAnnouncement);
                System.out.println("🔄 Announcement updated successfully!");

                // Affichage de l'annonce mise à jour
                System.out.println("📝 Updated Announcement: " + firstAnnouncement);
            } else {
                System.out.println("❌ No announcements available to update.");
            }

            // 4️⃣ Suppression de la dernière annonce (si disponible)
            if (!announcements.isEmpty()) {
                int announcementIdToDelete = announcements.get(announcements.size() - 1).getIdAnnouncement();
                announcementService.delete(announcementIdToDelete);
                System.out.println("🗑️ Announcement with ID " + announcementIdToDelete + " deleted successfully!");
            } else {
                System.out.println("❌ No announcements available to delete.");
            }

            // ===========================
            // 🎟️ Gestion des Réservations (Reservations)
            // ===========================

            //5️⃣ Ajout d'une nouvelle réservation
            Reservation newReservation = new Reservation(0, LocalDateTime.now(), Reservation.Status.CONFIRMED, "reservation CA", 2, 2, 2);
            reservationService.create(newReservation);
            System.out.println("✅ Reservation added successfully!");

            // 6️⃣ Récupération et affichage de toutes les réservations
            List<Reservation> reservations = reservationService.read();
            System.out.println("📜 All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }

            // 7️⃣ Mise à jour de la première réservation (si disponible)
            if (!reservations.isEmpty()) {
                Reservation firstReservation = reservations.get(0);
                firstReservation.setStatus(Reservation.Status.COMPLETED);
                firstReservation.setDescription("This reservation is updated.");
                reservationService.update(firstReservation);
                System.out.println("🔄 Reservation updated successfully!");

                // Affichage de la réservation mise à jour
                System.out.println("📝 Updated Reservation: " + firstReservation);
            } else {
                System.out.println("❌ No reservations available to update.");
            }

            // 8️⃣ Suppression de la dernière réservation (si disponible)
            if (!reservations.isEmpty()) {
                int reservationIdToDelete = reservations.get(reservations.size() - 1).getIdReservation();
                reservationService.delete(reservationIdToDelete);
                System.out.println("🗑️ Reservation with ID " + reservationIdToDelete + " deleted successfully!");
            } else {
                System.out.println("❌ No reservations available to delete.");
            }

            // ===========================
            // 🚚 Gestion des Déménagements (Relocations)
            // ===========================

            // 9️⃣ Ajout d'un nouveau déménagement
            Relocation newRelocation = new Relocation(2, 2, LocalDateTime.now(), true, 1925.0f);
            relocationService.create(newRelocation);
            System.out.println("✅ Relocation added successfully!");

            // 🔟 Récupération et affichage de tous les déménagements
            List<Relocation> relocations = relocationService.read();
            System.out.println("📜 All Relocations:");
            for (Relocation relocation : relocations) {
                System.out.println(relocation);
            }

            // 1️⃣1️⃣ Mise à jour du premier déménagement (si disponible)
            if (!relocations.isEmpty()) {
                Relocation firstRelocation = relocations.get(0);
                firstRelocation.setStatus(false);
                firstRelocation.setCost(1920.0f);
                relocationService.update(firstRelocation);
                System.out.println("🔄 Relocation updated successfully!");

                // Affichage du déménagement mis à jour
                System.out.println("📝 Updated Relocation: " + firstRelocation);
            } else {
                System.out.println("❌ No relocations available to update.");
            }

            // 1️⃣2️⃣ Suppression du dernier déménagement (si disponible)
            if (!relocations.isEmpty()) {
                int relocationIdToDelete = relocations.get(relocations.size() - 1).getIdRelocation();
                relocationService.delete(relocationIdToDelete);
                System.out.println("🗑️ Relocation with ID " + relocationIdToDelete + " deleted successfully!");
            } else {
                System.out.println("❌ No relocations available to delete.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ An error occurred while performing database operations: " + e.getMessage());
        }
    }
}