package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Station;
import org.wamiago.wamiago.services.BicycleService;
import org.wamiago.wamiago.services.StationService;
import org.wamiago.wamiago.utils.DataBase;

public class Main {
    public static void main(String[] args) {
        DataBase.getInstance().getConnection();
    }
}


