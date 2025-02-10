package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.BicycleRental;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BicycleRentalService implements IService<BicycleRental> {

    private final Connection connection;

    BicycleRentalService() {
        this.connection = DataBase.getInstance().getConnection();
    }
    @Override
    public void create(BicycleRental bicycleRental) throws SQLException {
        String sql = "INSERT INTO bicycle_rental ( id_user,id_bike,id_start_station,id_end_station, start_time, end_time,distance_km,battery_used,cost) VALUES (?,?,?,?,?,?,?,?,?)";
        connection.prepareStatement(sql);
        preparedStatement.setInt(1, bicycleRental.getUser());


    }

    @Override
    public void update(BicycleRental bicycleRental) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public List<BicycleRental> read() throws SQLException {
        return List.of();
    }
}
