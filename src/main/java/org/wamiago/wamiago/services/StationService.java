package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Location;
import org.wamiago.wamiago.entities.Station;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationService implements IService<Station> {
    private final Connection connection;

    public StationService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Station station) throws SQLException {
        String sql ="INSERT INTO bicycle_station (name,id_location,total_docks," +
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

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bicycle_station WHERE id_station=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();

    }

    @Override
    public List<Station> read() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_station";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
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

    public Station getById(int id) throws SQLException {
        String sql = "SELECT * FROM bicycle_station WHERE id_station = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
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

}
