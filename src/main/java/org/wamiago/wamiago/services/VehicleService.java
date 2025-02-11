package org.wamiago.wamiago.services;

import org.wamiago.wamiago.entities.Vehicle;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleService implements IService<Vehicle> {
    private final Connection connection;

    public VehicleService() {
        connection = DataBase.getInstance().getConnection();
    }

    @Override
    public void create(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle (id_driver, registration, color, model, brand) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, vehicle.getIdDriver());
        preparedStatement.setInt(2, vehicle.getRegistration());
        preparedStatement.setString(3, vehicle.getColor());
        preparedStatement.setString(4, vehicle.getModel());
        preparedStatement.setString(5, vehicle.getBrand());
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicle SET id_driver = ?, registration = ?, color = ?, model = ?, brand = ? WHERE id_vehicle = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, vehicle.getIdDriver());
        preparedStatement.setInt(2, vehicle.getRegistration());
        preparedStatement.setString(3, vehicle.getColor());
        preparedStatement.setString(4, vehicle.getModel());
        preparedStatement.setString(5, vehicle.getBrand());
        preparedStatement.setInt(6, vehicle.getIdVehicle());
        preparedStatement.executeUpdate();
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE id_vehicle = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Vehicle> read() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            Vehicle vehicle = new Vehicle();
            vehicle.setIdVehicle(rs.getInt("id_vehicle"));
            vehicle.setIdDriver(rs.getInt("id_driver"));
            vehicle.setRegistration(rs.getInt("registration"));
            vehicle.setColor(rs.getString("color"));
            vehicle.setModel(rs.getString("model"));
            vehicle.setBrand(rs.getString("brand"));
            vehicles.add(vehicle);
        }
        return vehicles;
    }
}  

