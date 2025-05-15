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
    public boolean create(Location location) throws SQLException {
        String sql = "INSERT INTO location (address,latitude,longitude) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, location.getAddress());
        preparedStatement.setDouble(2, location.getLatitude());
        preparedStatement.setDouble(3, location.getLongitude());
        preparedStatement.executeUpdate();
        return false;
    }
//St7a9itha fil StationController
    public Location createLocation(Location location)throws SQLException
    {
        String sql = "INSERT INTO location (address,latitude,longitude) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, location.getAddress());
        preparedStatement.setDouble(2, location.getLatitude());
        preparedStatement.setDouble(3, location.getLongitude());
        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            location.setId(rs.getInt(1));
            return location;
        }
        return null;

    }

    @Override
    public void update(Location location) throws SQLException {
        String sql = "UPDATE location SET address=?,latitude=?,longitude=? WHERE id_location=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, location.getAddress());
        preparedStatement.setDouble(2, location.getLatitude());
        preparedStatement.setDouble(3, location.getLongitude());
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

    public Location getByAddress(String address) throws SQLException {
        String sql = "SELECT * FROM location WHERE address = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, address);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            Location location = new Location();
            location.setId(rs.getInt("id_location"));
            location.setAddress(rs.getString("address"));
            return location;
        }
        return null;
    }





}
