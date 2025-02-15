package services;

import entities.Location;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService implements IService<Location> {
    private final Connection connection;

    public LocationService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Location location) throws SQLException {
        String sql = "INSERT INTO location (address,latitude,longitude) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, location.getAddress());
        preparedStatement.setFloat(2, location.getLatitude());
        preparedStatement.setFloat(3, location.getLongitude());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Location location) throws SQLException {
        String sql = "UPDATE location SET address=?,latitude=?,longitude=? WHERE id_location=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, location.getAddress());
        preparedStatement.setFloat(2, location.getLatitude());
        preparedStatement.setFloat(3, location.getLongitude());
        preparedStatement.setInt(4, location.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM location WHERE id_location=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Location> read() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM location";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Location location = new Location();
            location.setId(rs.getInt("id_location"));
            location.setAddress(rs.getString("address"));
            location.setLatitude(rs.getFloat("latitude"));
            location.setLongitude(rs.getFloat("longitude"));
            locations.add(location);
        }
        return locations;
    }
    public Location getById(int id) throws SQLException {
        String sql = "SELECT * FROM location WHERE id_location=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            Location location = new Location();
            location.setId(rs.getInt("id_location"));
            location.setAddress(rs.getString("address"));
            location.setLatitude(rs.getFloat("latitude"));
            location.setLongitude(rs.getFloat("longitude"));
            return location;
        }
        return null;
    }
    // Method to calculate the distance between two Location objects using the Haversine formula
    public double calculateDistance(Location location1, Location location2) {
        final int R = 6371; // Radius of the Earth in km

        double lat1 = location1.getLatitude();
        double lon1 = location1.getLongitude();
        double lat2 = location2.getLatitude();
        double lon2 = location2.getLongitude();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Returns the distance in kilometers
    }

}
