package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Bicycle;
import org.wamiago.wamiago.entities.Station;
import org.wamiago.wamiago.services.BicycleRentalService;
import org.wamiago.wamiago.services.BicycleService;
import org.wamiago.wamiago.services.LocationService;
import org.wamiago.wamiago.services.StationService;
import org.wamiago.wamiago.utils.DataBase;

import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        DataBase.getInstance().getConnection();
        StationService stationService = new StationService();
        BicycleService bicycleService = new BicycleService();
        BicycleRentalService bicycleRentalService = new BicycleRentalService();

        try {
            // bicycle = new Bicycle(0, stationService.getById(2), Bicycle.STATUS.available, 100, 1000, Timestamp.valueOf("2021-06-01 00:00:00"));
            //bicycleService.create(bicycle);
            //.setStation(stationService.getById(3));
            //bicycleService.update(bicycle);
            //bicycleService.delete(8);
            //Station station = new Station(0, "Avenue Habib Bourguiba, Tunis n2", new LocationService().getById(1), 10, 10, 10, 10, Station.STATUS.active);
            //stationService.create(station);
            //station.setName("Avenue Habib Bourguiba, Tunis n3");
            //stationService.update(station);
            //stationService.delete(8);
            //BicycleRental bicycleRental = new BicycleRental(0, new UserService().getById(1), new BicycleService().getById(1), new StationService().getById(1), new StationService().getById(2), Timestamp.valueOf("2021-06-01 00:00:00"), Timestamp.valueOf("2021-06-01 00:00:00"), 10, 10, 10);
            //bicycleRentalService.create(bicycleRental);
            //bicycleRental.setStart_station(new StationService().getById(3));
            //bicycleRentalService.update(bicycleRental);
            //bicycleRentalService.delete(8);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}