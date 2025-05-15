package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DataBase;

public class DashboardService {
    private Connection connection;

    public DashboardService() {
        connection = DataBase.getInstance().getConnection();
    }

    public int getTotalUsers() {
        return fetchCount("SELECT COUNT(*) FROM user");
    }

    public int getTotalAnnouncements() {
        return fetchCount("SELECT COUNT(*) FROM announcement");
    }

    public int getTotalRelocations() {
        return fetchCount("SELECT COUNT(*) FROM relocation");
    }

    public int getTotalReservations() {
        return fetchCount("SELECT COUNT(*) FROM reservation");
    }

    public int getOngoingReservation() {
        return fetchCount("SELECT COUNT(*) FROM reservation WHERE status = 4");
    }

    public int getCompletedReservation() {
        return fetchCount("SELECT COUNT(*) FROM reservation WHERE status = 3");
    }

    public int getCanceledReservation() {
        return fetchCount("SELECT COUNT(*) FROM reservation WHERE status = 2");
    }


    public int getCONFIRMEDReservation() {
        return fetchCount("SELECT COUNT(*) FROM reservation WHERE status = 1");
    }

    private int fetchCount(String query) {
        int count = 0;
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}