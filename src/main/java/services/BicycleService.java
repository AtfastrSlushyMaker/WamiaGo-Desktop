package services;

import entities.Bicycle;
import entities.Station;
import utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BicycleService implements IService<Bicycle> {
    private final Connection connection;

    public BicycleService() {
        this.connection = DataBase.getInstance().getConnection();
    }

    @Override
    public boolean create(Bicycle bicycle) throws SQLException {
        String sql = "INSERT INTO bicycle ( id_station, status, battery_level, range_km, last_updated) VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bicycle.getStation().getId());
        preparedStatement.setString(2, bicycle.getStatus().toString());
        preparedStatement.setFloat(3, bicycle.getBattery_level());
        preparedStatement.setFloat(4, bicycle.getRange_km());
        preparedStatement.setObject(5, bicycle.getLast_updated());
        preparedStatement.executeUpdate();
        System.out.println("✅ Bicycle created successfully");
        return false;
    }

    @Override
    public void update(Bicycle bicycle) throws SQLException {
        String sql = "UPDATE bicycle SET id_station = ?, status = ?, battery_level = ?, range_km = ?, last_updated = ? WHERE id_bike = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bicycle.getStation().getId());
        preparedStatement.setString(2, bicycle.getStatus().toString());
        preparedStatement.setFloat(3, bicycle.getBattery_level());
        preparedStatement.setFloat(4, bicycle.getRange_km());
        preparedStatement.setObject(5, bicycle.getLast_updated());
        preparedStatement.setInt(6, bicycle.getId());
        preparedStatement.executeUpdate();
        System.out.println("✅ Bicycle updated successfully");

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bicycle WHERE id_bike= ?";
        connection.prepareStatement(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("✅ Bicycle deleted successfully");

    }

    @Override
    public List<Bicycle> read() throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Bicycle bicycle = new Bicycle();
            bicycle.setId(rs.getInt("id_bike"));
            bicycle.setStation(new StationService().getById(rs.getInt("id_station")));
            bicycle.setStatus(Bicycle.STATUS.valueOf(rs.getString("status")));
            bicycle.setBattery_level(rs.getFloat("battery_level"));
            bicycle.setRange_km(rs.getFloat("range_km"));
            bicycle.setLast_updated(rs.getTimestamp("last_updated"));
            bicycles.add(bicycle);
        }

        return bicycles;

    }
    public Bicycle getById(int id) throws SQLException {
        String sql = "SELECT * FROM bicycle WHERE id_bike = ?";
        StationService stationService = new StationService();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bicycle bicycle = new Bicycle();
                bicycle.setId(rs.getInt("id_bike"));
                bicycle.setStation(stationService.getById(rs.getInt("id_station")));
                bicycle.setStatus(Bicycle.STATUS.valueOf(rs.getString("status")));
                bicycle.setBattery_level(rs.getFloat("battery_level"));
                bicycle.setRange_km(rs.getFloat("range_km"));
                bicycle.setLast_updated(rs.getTimestamp("last_updated"));
                return bicycle;
            }
        }
        return null;
    }
    public List<Bicycle> getByStation(Station station) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE id_station = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, station.getId());
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
        }
        return bicycles;
    }
    public List<Bicycle> getByStatus(Bicycle.STATUS status) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE status = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toString());
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
        }
        return bicycles;
    }
    public List<Bicycle> getByBatteryLevel(float battery_level) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE battery_level = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setFloat(1, battery_level);
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
        }
        return bicycles;
    }
    public List<Bicycle> getByRangeKm(float range_km) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE range_km = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setFloat(1, range_km);
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
        }
        return bicycles;
    }
    public List<Bicycle> getByLastUpdated(Timestamp last_updated) throws SQLException {
        List<Bicycle> bicycles = new ArrayList<>();
        String sql = "SELECT * FROM bicycle WHERE last_updated = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, last_updated);
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
        }
        return bicycles;
    }
    public void batchUpdate(List<Bicycle> bicycles) throws Exception {
        for (Bicycle bicycle : bicycles) {
            update(bicycle);
        }
    }

    public void batchDelete(List<Integer> bicycleIds) {
        for (int id : bicycleIds) {
            try {
                delete(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}