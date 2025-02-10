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
            Announcement newAnnouncement = new Announcement(0, 1, "New Route", "We have added a new route to the app!", LocalDateTime.now(), Announcement.Zone.Tunis, 1);
            Announcement newAnnouncement2 = new Announcement(1, 1, "New Route", "Non Se Compara!", LocalDateTime.now(), Announcement.Zone.Kairouan, 2);

            announcementService.create(newAnnouncement);
            announcementService.create(newAnnouncement2);
            System.out.println("✅ Announcement added successfully!");

            // 2️⃣ Récupération et affichage de toutes les annonces
            List<Announcement> announcements = announcementService.read();
            System.out.println("📜 All Announcements:");
            for (Announcement announcement : announcements) {
                System.out.println(announcement);
            }

            // 3️⃣ Mise à jour de la première annonce (si disponible)
            if (!announcements.isEmpty()) {
                Announcement firstAnnouncement = announcements.get(5);
                firstAnnouncement.setTitle("Updated Route");
                firstAnnouncement.setContent("This route has been updated.");
                firstAnnouncement.setDate(LocalDateTime.now());
                firstAnnouncement.setStatus(0);
                announcementService.update(firstAnnouncement);
                System.out.println("🔄 Announcement updated successfully!");

                // Affichage de l'annonce mise à jour
                Announcement updatedAnnouncement = announcements.get(0);
                System.out.println("📝 Updated Announcement: " + updatedAnnouncement);
            }

            // 4️⃣ Suppression de la dernière annonce
            if (!announcements.isEmpty()) {
                int announcementIdToDelete = announcements.get(announcements.size() - 1).getIdAnnouncement();
                announcementService.delete(announcementIdToDelete);
                System.out.println("🗑️ Announcement with ID " + announcementIdToDelete + " deleted successfully!");
            }

            // ===========================
            // 🎟️ Gestion des Réservations (Reservations)
            // ===========================

          // 5️⃣ Ajout d'une nouvelle réservation
            Reservation newReservation2 = new Reservation(0, LocalDateTime.now(), Reservation.Status.CONFIRMED, "Second reservation", 2, 2, 2);
            reservationService.create(newReservation2);
            System.out.println("✅ Reservation added successfully!");

            // 6️⃣ Récupération et affichage de toutes les réservations
            List<Reservation> reservations = reservationService.read();
            System.out.println("📜 All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }

            // 7️⃣ Mise à jour de la première réservation (si disponible)
            if (!reservations.isEmpty()) {
                Reservation firstReservation = reservations.get(2);
                firstReservation.setStatus(Reservation.Status.COMPLETED);
                firstReservation.setDescription("This reservation is updated.");
                reservationService.update(firstReservation);
                System.out.println("🔄 Reservation updated successfully!");

                // Affichage de la réservation mise à jour
                Reservation updatedReservation = reservations.get(0);
                System.out.println("📝 Updated Reservation: " + updatedReservation);
            }

            // 8️⃣ Suppression de la dernière réservation
            if (!reservations.isEmpty()) {
                int reservationIdToDelete = reservations.get(reservations.size() - 1).getIdReservation();
                reservationService.delete(reservationIdToDelete);
                System.out.println("🗑️ Reservation with ID " + reservationIdToDelete + " deleted successfully!");
            }

            // ===========================
            // 🚚 Gestion des Déménagements (Relocations)
            // ===========================

//            // 9️⃣ Ajout d'un nouveau déménagement
            Relocation newRelocation = new Relocation(2, LocalDateTime.now(), 2, 2002.0f);
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
                Relocation firstRelocation = relocations.get(1);
                firstRelocation.setStatus(0);
                firstRelocation.setCost(1920.0f);
                relocationService.update(firstRelocation);
                System.out.println("🔄 Relocation updated successfully!");

                // Affichage du déménagement mis à jour
                Relocation updatedRelocation = relocations.get(0);
                System.out.println("📝 Updated Relocation: " + updatedRelocation);
            }

            // 1️⃣2️⃣ Suppression du dernier déménagement
            if (!relocations.isEmpty()) {
                int relocationIdToDelete = relocations.get(relocations.size() - 1).getIdRelocation();
                relocationService.delete(relocationIdToDelete);
                System.out.println("🗑️ Relocation with ID " + relocationIdToDelete + " deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
