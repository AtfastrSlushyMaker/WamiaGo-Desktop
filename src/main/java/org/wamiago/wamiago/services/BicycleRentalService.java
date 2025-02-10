package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.BicycleRental;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BicycleRentalService implements IService<BicycleRental> {

    private final Connection connection;

    BicycleRentalService() {
        this.connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void create(BicycleRental bicycleRental) throws SQLException {
        String sql = "INSERT INTO bicycle_rental ( id_user,id_bike,id_start_station,id_end_station, start_time, end_time,distance_km,battery_used,cost) VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bicycleRental.getUser().getId());
        preparedStatement.setInt(2, bicycleRental.getBicycle().getId());
        preparedStatement.setInt(3, bicycleRental.getStart_station().getId());
        preparedStatement.setInt(4, bicycleRental.getEnd_station().getId());
        preparedStatement.setObject(5, bicycleRental.getStart_time());
        preparedStatement.setObject(6, bicycleRental.getEnd_time());
        preparedStatement.setFloat(7, bicycleRental.getDistance_km());
        preparedStatement.setFloat(8, bicycleRental.getBattery_used());
        preparedStatement.setFloat(9, bicycleRental.getCost());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(BicycleRental bicycleRental) throws SQLException {
        String sql = "UPDATE bicycle_rental SET id_user = ?, id_bike = ?, id_start_station = ?, id_end_station = ?, start_time = ?, end_time = ?, distance_km = ?, battery_used = ?, cost = ? WHERE id_user_rental = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bicycleRental.getUser().getId());
        preparedStatement.setInt(2, bicycleRental.getBicycle().getId());
        preparedStatement.setInt(3, bicycleRental.getStart_station().getId());
        preparedStatement.setInt(4, bicycleRental.getEnd_station().getId());
        preparedStatement.setObject(5, bicycleRental.getStart_time());
        preparedStatement.setObject(6, bicycleRental.getEnd_time());
        preparedStatement.setFloat(7, bicycleRental.getDistance_km());
        preparedStatement.setFloat(8, bicycleRental.getBattery_used());
        preparedStatement.setFloat(9, bicycleRental.getCost());
        preparedStatement.setInt(10, bicycleRental.getId());
        preparedStatement.executeUpdate();

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bicycle_rental WHERE id_user_rental= ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<BicycleRental> read() throws SQLException {
        List<BicycleRental> bicycleRentals = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_rental";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            BicycleRental bicycleRental = new BicycleRental(
                    rs.getInt("id_user_rental"),
                    new UserService().getUserById(rs.getInt("id_user")),
                    new BicycleService().getBicycleById(rs.getInt("id_bike")),
                    new StationService().getStationById(rs.getInt("id_start_station")),
                    new StationService().getStationById(rs.getInt("id_end_station")),
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getTimestamp("end_time").toLocalDateTime(),
                    rs.getFloat("distance_km"),
                    rs.getFloat("battery_used"),
                    rs.getFloat("cost")
            );
            bicycleRentals.add(bicycleRental);
        }
        return bicycleRentals;
    }
}
