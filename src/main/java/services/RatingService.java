package services;

import entities.Rating;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingService implements IService<Rating> {

    private final Connection connection;

    public RatingService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Rating rating) throws SQLException {
        String sql = "INSERT INTO `rating`(`id_user`, `id_driver`, `comment`, `rating`) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rating.getUserId());
            ps.setInt(2, rating.getDriverId());
            ps.setString(3, rating.getComment());
            ps.setInt(4, rating.getRating());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rating.setIdRating(rs.getInt(1));
                }
            }
        }
        return false;
    }

    @Override
    public void update(Rating rating) throws SQLException {
        String sql = "UPDATE `rating` SET `comment`=?, `rating`=? WHERE `id_rating`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, rating.getComment());
            ps.setInt(2, rating.getRating());
            ps.setInt(3, rating.getIdRating());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM `rating` WHERE `id_rating`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Rating> read() throws SQLException {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM `rating`";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Rating rating = new Rating(
                        rs.getInt("id_rating"),
                        rs.getInt("id_user"),
                        rs.getInt("id_driver"),
                        rs.getString("comment"),
                        rs.getInt("rating")
                );
                ratings.add(rating);
            }
        }
        return ratings;
    }

    public List<Rating> getRatingsByDriver(int driverId) throws SQLException {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM `rating` WHERE `id_driver`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(new Rating(
                            rs.getInt("id_rating"),
                            rs.getInt("id_user"),
                            rs.getInt("id_driver"),
                            rs.getString("comment"),
                            rs.getInt("rating")
                    ));
                }
            }
        }
        return ratings;
    }

    public double getAverageRatingByDriver(int driverId) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM `rating` WHERE `id_driver`=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        }
        return 0.0;
    }
    public List<Object[]> getTopRatedDrivers(int limit) throws SQLException {
        List<Object[]> leaderboard = new ArrayList<>();
        String sql = "SELECT d.id_driver, u.name, AVG(r.rating) AS avg_rating, COUNT(r.id_rating) AS total_ratings " +
                "FROM `rating` r " +
                "JOIN `driver` d ON r.id_driver = d.id_driver " +
                "JOIN `user` u ON d.id_user = u.id_user " +
                "GROUP BY d.id_driver, u.name " +
                "ORDER BY avg_rating DESC, total_ratings DESC " +
                "LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    leaderboard.add(new Object[]{
                            rs.getInt("id_driver"),
                            rs.getString("name"),
                            rs.getDouble("avg_rating"),
                            rs.getInt("total_ratings")
                    });
                }
            }
        }
        return leaderboard;
    }

}
