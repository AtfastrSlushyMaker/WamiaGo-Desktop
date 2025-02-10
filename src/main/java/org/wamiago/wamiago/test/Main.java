package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Announcement;
//import org.wamiago.wamiago.entities.Driver;
import org.wamiago.wamiago.services.AnnouncementService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnnouncementService announcementService = new AnnouncementService();

        try {
            // 1️⃣ ADD a new announcement
            //Driver transporter = new Driver(1, "John Doe", "12345");
            Announcement newAnnouncement = new Announcement(0, 1, "New Route", "We have added a new route to the app!", LocalDateTime.now(), Announcement.Zone.Tunis, 1);
            announcementService.create(newAnnouncement);
            System.out.println("✅ Announcement added successfully!");

//            // 2️⃣ FETCH all announcements
//            List<Announcement> announcements = announcementService.read();
//            System.out.println("📜 All Announcements:");
//            for (Announcement announcement : announcements) {
//                System.out.println(announcement);
//            }
//
//            // 3️⃣ UPDATE the first announcement (if exists)
//            if (!announcements.isEmpty()) {
//                Announcement firstAnnouncement = announcements.get(0);
//                firstAnnouncement.setTitle("Updated Route");
//                firstAnnouncement.setContent("This route has been updated.");
//                firstAnnouncement.setDate(LocalDateTime.now());
//                firstAnnouncement.setStatus(0);
//                announcementService.update(firstAnnouncement);
//                System.out.println("🔄 Announcement updated successfully!");
//
//                // Fetch and print updated announcement
//                Announcement updatedAnnouncement = announcements.get(0);
//                System.out.println("📝 Updated Announcement: " + updatedAnnouncement);
//            }
//
//            // 4️⃣ DELETE an announcement by ID
//            if (!announcements.isEmpty()) {
//                int announcementIdToDelete = announcements.get(announcements.size() - 1).getIdAnnouncement();
//                announcementService.delete(announcementIdToDelete);
//                System.out.println("🗑️ Announcement with ID " + announcementIdToDelete + " deleted successfully!");
//            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}