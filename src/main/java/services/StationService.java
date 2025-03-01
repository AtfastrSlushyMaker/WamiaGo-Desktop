package services;

import entities.Bicycle;
import entities.Location;
import entities.Station;
import entities.User;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StationService implements IService<Station> {
    private final Connection connection;

    public StationService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Station station) throws SQLException {
        String sql = "INSERT INTO bicycle_station (name,id_location,total_docks," +
                "available_docks,available_bikes,charging_bikes,status) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, station.getName());
        preparedStatement.setInt(2, station.getLocation().getId());
        preparedStatement.setInt(3, station.getTotal_docks());
        preparedStatement.setInt(4, station.getAvailable_docks());
        preparedStatement.setInt(5, station.getAvailable_bikes());
        preparedStatement.setInt(6, station.getCharging_bikes());
        preparedStatement.setString(7, station.getStatus().toString());
        preparedStatement.executeUpdate();
        System.out.println("✅ Station created successfully.");
        return false;
    }


    @Override
    public void update(Station station) throws SQLException {
        String sql = "UPDATE bicycle_station SET name=?,id_location=?,total_docks=?," +
                "available_docks=?,available_bikes=?,charging_bikes=?,status=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, station.getName());
        preparedStatement.setInt(2, station.getLocation().getId());
        preparedStatement.setInt(3, station.getTotal_docks());
        preparedStatement.setInt(4, station.getAvailable_docks());
        preparedStatement.setInt(5, station.getAvailable_bikes());
        preparedStatement.setInt(6, station.getCharging_bikes());
        preparedStatement.setString(7, station.getStatus().toString());
        preparedStatement.setInt(8, station.getId());
        preparedStatement.executeUpdate();
        System.out.println("✅ Station updated successfully.");

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bicycle_station WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Station deleted successfully.");

    }

    @Override
    public List<Station> read() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_station";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        LocationService locationService = new LocationService();
        while (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id_station"));
            station.setName(rs.getString("name"));
            station.setLocation(locationService.getById(rs.getInt("id_location")));
            station.setTotal_docks(rs.getInt("total_docks"));
            station.setAvailable_docks(rs.getInt("available_docks"));
            station.setAvailable_bikes(rs.getInt("available_bikes"));
            station.setCharging_bikes(rs.getInt("charging_bikes"));
            station.setStatus(Station.STATUS.valueOf(rs.getString("status")));
            stations.add(station);
        }
        return stations;
    }

    public Station getById(int id) throws SQLException {
        String sql = "SELECT * FROM bicycle_station WHERE id_station = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id_station"));
            station.setName(rs.getString("name"));
            station.setLocation(new LocationService().getById(rs.getInt("id_location")));
            station.setTotal_docks(rs.getInt("total_docks"));
            station.setAvailable_docks(rs.getInt("available_docks"));
            station.setAvailable_bikes(rs.getInt("available_bikes"));
            station.setCharging_bikes(rs.getInt("charging_bikes"));
            station.setStatus(Station.STATUS.valueOf(rs.getString("status")));
            return station;
        }
        return null;
    }

    public Station getByName(String name) throws SQLException {
        String sql = "SELECT * FROM bicycle_station WHERE name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id_station"));
            station.setName(rs.getString("name"));
            station.getLocation().setId(rs.getInt("id_location"));
            station.setTotal_docks(rs.getInt("total_docks"));
            station.setAvailable_docks(rs.getInt("available_docks"));
            station.setAvailable_bikes(rs.getInt("available_bikes"));
            station.setCharging_bikes(rs.getInt("charging_bikes"));
            station.setStatus(Station.STATUS.valueOf(rs.getString("status")));
            return station;
        }
        return null;
    }

    public List<Station> getByLocation(Location location) throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_station WHERE id_location = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, location.getId());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id_station"));
            station.setName(rs.getString("name"));
            station.getLocation().setId(rs.getInt("id_location"));
            station.setTotal_docks(rs.getInt("total_docks"));
            station.setAvailable_docks(rs.getInt("available_docks"));
            station.setAvailable_bikes(rs.getInt("available_bikes"));
            station.setCharging_bikes(rs.getInt("charging_bikes"));
            station.setStatus(Station.STATUS.valueOf(rs.getString("status")));
            stations.add(station);
        }
        return stations;
    }

    public void updateAvailableBikes(Station station, int available_bikes) throws SQLException {
        String sql = "UPDATE bicycle_station SET available_bikes=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, available_bikes);
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public void updateAvailableDocks(Station station, int available_docks) throws SQLException {
        String sql = "UPDATE bicycle_station SET available_docks=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, available_docks);
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public void updateChargingBikes(Station station, int charging_bikes) throws SQLException {
        String sql = "UPDATE bicycle_station SET charging_bikes=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, charging_bikes);
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public void updateTotalDocks(Station station, int total_docks) throws SQLException {
        String sql = "UPDATE bicycle_station SET total_docks=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, total_docks);
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public void updateStatus(Station station, Station.STATUS status) throws SQLException {
        String sql = "UPDATE bicycle_station SET status=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, status.toString());
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public void updateLocation(Station station, Location location) throws SQLException {
        String sql = "UPDATE bicycle_station SET id_location=? WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, location.getId());
        preparedStatement.setInt(2, station.getId());
        preparedStatement.executeUpdate();
    }

    public List<Station> sortById() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> (s1.getId() - s2.getId()));
        return stations;
    }

    public List<Station> sortByName() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
        return stations;
    }

    public List<Station> sortByAvailableBikes() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> s2.getAvailable_bikes() - s1.getAvailable_bikes());
        return stations;
    }

    public List<Station> sortByAvailableDocks() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> s2.getAvailable_docks() - s1.getAvailable_docks());
        return stations;
    }

    public List<Station> sortByChargingBikes() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> s2.getCharging_bikes() - s1.getCharging_bikes());
        return stations;
    }

    public List<Station> sortByTotalDocks() throws SQLException {
        List<Station> stations = read();
        stations.sort((s1, s2) -> s2.getTotal_docks() - s1.getTotal_docks());
        return stations;
    }

    public List<Station> search(String by, String value) throws SQLException {
        List<Station> stations = new ArrayList<>();

        // Whitelist of allowed column names to prevent SQL injection
        List<String> allowedColumns = List.of("name", "status"); // Add valid column names
        if (!allowedColumns.contains(by)) {
            throw new IllegalArgumentException("Invalid search column: " + by);
        }

        String sql = "SELECT * FROM bicycle_station WHERE " + by + " LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "%" + value + "%");  // Correct LIKE usage

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Station station = new Station();
            station.setId(rs.getInt("id_station"));
            station.setName(rs.getString("name"));
            station.setLocation(new LocationService().getById(rs.getInt("id_location")));
            station.setTotal_docks(rs.getInt("total_docks"));
            station.setAvailable_docks(rs.getInt("available_docks"));
            station.setAvailable_bikes(rs.getInt("available_bikes"));
            station.setCharging_bikes(rs.getInt("charging_bikes"));
            station.setStatus(Station.STATUS.valueOf(rs.getString("status")));
            stations.add(station);
        }

        rs.close();
        preparedStatement.close();

        return stations;
    }


    public List<Bicycle> getAvailableBikes(Station station) {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE id_station = ? AND status = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, station.getId());
            ps.setString(2, Bicycle.STATUS.available.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bicycle bicycle = new Bicycle();
                bicycle.setId(rs.getInt("id_bike"));
                bicycle.getStation().setId(rs.getInt("id_station"));
                bicycle.setStatus(Bicycle.STATUS.valueOf(rs.getString("status")));
                bicycle.setBattery_level(rs.getFloat("battery_level"));
                bicycle.setRange_km(rs.getFloat("range_km"));
                bicycle.setLast_updated(rs.getTimestamp("last_updated"));
                bicycles.add(bicycle);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return bicycles;
    }

    public void fixDataBaseBicycles() {
        try {
        List <Station> stations = read();
        for(Station station: stations){
            List<Bicycle> bicycles = getAvailableBikes(station);
            updateAvailableBikes(station, bicycles.size());
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllStationNames() throws SQLException {
        List<String> stationNames = new ArrayList<>();
        String sql = "SELECT name FROM bicycle_station";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            stationNames.add(rs.getString("name"));
        }
        return stationNames;
    }

    public List<Station> getSortedStationsByUserDistance(User user) throws SQLException {
        List<Station> stations = read();
        stations.sort(Comparator.<Station>comparingDouble(s ->Location.calculateDistance(user.getLocation(),s.getLocation())).reversed());
        return stations;
    }

}
