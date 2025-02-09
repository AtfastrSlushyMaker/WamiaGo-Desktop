package org.wamiago.wamiago.test;

import org.wamiago.wamiago.entities.Bicycle;
import org.wamiago.wamiago.services.BicycleService;
import org.wamiago.wamiago.utils.DataBase;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataBase.getInstance();
        BicycleService bicycleService = new BicycleService();
        Bicycle bicycle = new Bicycle();
        bicycle.getStation().setId(1);
        try {
            bicycleService.create(bicycle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        List<Bicycle> bicycles = bicycleService.read();
        for (Bicycle b : bicycles) {
            System.out.println(b);}
        }
        catch (Exception e){
            e.printStackTrace();}
}
}
