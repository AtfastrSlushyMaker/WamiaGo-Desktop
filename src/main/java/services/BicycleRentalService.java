package services;
import entities.Bicycle;
import entities.BicycleRental;
import entities.Station;
import entities.User;
import utils.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BicycleRentalService implements IService<BicycleRental> {
    private final Connection connection;

    public BicycleRentalService() {
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
        System.out.println("✅ BicycleRental created successfully.");
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
        System.out.println("✅ BicycleRental updated successfully.");

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
        BicycleRental bicycleRental=new BicycleRental();
        BicycleService bicycleService = new BicycleService();
        StationService stationService = new StationService();
        UserService userService = new UserService();

        while (rs.next()) {
            bicycleRental.setBicycle(bicycleService.getById(rs.getInt("id_bike")));
            bicycleRental.setStart_station(stationService.getById(rs.getInt("id_start_station")));
            bicycleRental.setEnd_station(stationService.getById(rs.getInt("id_end_station")));
            bicycleRental.setUser(userService.getById(rs.getInt("id_user")));
            bicycleRental.setStart_time(rs.getTimestamp("start_time"));
            bicycleRental.setEnd_time(rs.getTimestamp("end_time"));
            bicycleRental.setDistance_km(rs.getFloat("distance_km"));
            bicycleRental.setBattery_used(rs.getFloat("battery_used"));
            bicycleRental.setCost(rs.getFloat("cost"));
            bicycleRentals.add(bicycleRental);

        }
        return bicycleRentals;
    }
    public BicycleRental getById(int id) throws SQLException
    {
        String sql="SELECT * FROM bicycle_rental WHERE id_user_rental=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setInt(1,id);
        ResultSet rs=preparedStatement.executeQuery();
        while (rs.next())
        {
            return new BicycleRental(
                    rs.getInt("id_user_rental"),
                    new UserService().getById(rs.getInt("id_user")),
                    new BicycleService().getById(rs.getInt("id_bike")),
                    new StationService().getById(rs.getInt("id_start_station")),
                    new StationService().getById(rs.getInt("id_end_station")),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time"),
                    rs.getFloat("distance_km"),
                    rs.getFloat("battery_used"),
                    rs.getFloat("cost")
            );
        }
        return null;
    }
    public List<BicycleRental> getByUser(User user) throws SQLException
    {
        List<BicycleRental> bicycleRentals = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_rental WHERE id_user=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setInt(1,user.getId());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            BicycleRental bicycleRental = new BicycleRental(
                    rs.getInt("id_user_rental"),
                    new UserService().getById(rs.getInt("id_user")),
                    new BicycleService().getById(rs.getInt("id_bike")),
                    new StationService().getById(rs.getInt("id_start_station")),
                    new StationService().getById(rs.getInt("id_end_station")),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time"),
                    rs.getFloat("distance_km"),
                    rs.getFloat("battery_used"),
                    rs.getFloat("cost")
            );
            bicycleRentals.add(bicycleRental);
        }
        return bicycleRentals;
    }
    public List<BicycleRental> getByBicycle(Bicycle bicycle) throws SQLException
    {
        List<BicycleRental> bicycleRentals = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_rental WHERE id_bike=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setInt(1,bicycle.getId());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            BicycleRental bicycleRental = new BicycleRental(
                    rs.getInt("id_user_rental"),
                    new UserService().getById(rs.getInt("id_user")),
                    new BicycleService().getById(rs.getInt("id_bike")),
                    new StationService().getById(rs.getInt("id_start_station")),
                    new StationService().getById(rs.getInt("id_end_station")),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time"),
                    rs.getFloat("distance_km"),
                    rs.getFloat("battery_used"),
                    rs.getFloat("cost")
            );
            bicycleRentals.add(bicycleRental);
        }
        return bicycleRentals;
    }
    public List<BicycleRental> getByStartStation(Station station) throws SQLException
    {
        List<BicycleRental> bicycleRentals = new ArrayList<>();
        String sql = "SELECT * FROM bicycle_rental WHERE id_start_station=?";
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        preparedStatement.setInt(1,station.getId());
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            BicycleRental bicycleRental = new BicycleRental(
                    rs.getInt("id_user_rental"),
                    new UserService().getById(rs.getInt("id_user")),
                    new BicycleService().getById(rs.getInt("id_bike")),
                    new StationService().getById(rs.getInt("id_start_station")),
                    new StationService().getById(rs.getInt("id_end_station")),
                    rs.getTimestamp("start_time"),
                    rs.getTimestamp("end_time"),
                    rs.getFloat("distance_km"),
                    rs.getFloat("battery_used"),
                    rs.getFloat("cost")
            );
            bicycleRentals.add(bicycleRental);
        }
        return bicycleRentals;
    }

}
