package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;

public class BicycleRental {
    private int id;
    //private User user;
    private Bicycle bicycle;
    private Station start_station;
    private Station end_station;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private float distance_km;
    private float battery_used;
    private float cost;


}
