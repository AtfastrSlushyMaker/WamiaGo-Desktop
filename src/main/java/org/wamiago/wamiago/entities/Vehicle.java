package org.wamiago.wamiago.entities;

public class Vehicle {
    private int idVehicle;
    private int idDriver;
    private int registration;
    private String color;
    private String model;
    private String brand;


    public Vehicle(int idVehicle, int idDriver, int registration, String color, String model, String brand) {
        this.idVehicle = idVehicle;
        this.idDriver = idDriver;
        this.registration = registration;
        this.color = color;
        this.model = model;
        this.brand = brand;
    }


    public Vehicle() {}


    public int getIdVehicle() {
        return idVehicle;
    }

    public void setIdVehicle(int idVehicle) {
        this.idVehicle = idVehicle;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public int getRegistration() {
        return registration;
    }

    public void setRegistration(int registration) {
        this.registration = registration;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    @Override
    public String toString() {
        return "Vehicle{" +
                "idVehicle=" + idVehicle +
                ", idDriver=" + idDriver +
                ", registration=" + registration +
                ", color='" + color + '\'' +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
