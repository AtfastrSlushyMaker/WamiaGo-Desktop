package org.wamiago.wamiago.entities;

public class Driver extends User {
    private int id_driver;
    private int permit_number;
    private DriverRole driverRole;
    private int status;

    public enum DriverRole {
        TAXI_DRIVER,
        TRANSPORTER,
        CARPOOL_DRIVER
    }
    public Driver(int id_driver, String name, String email, String phone, String password, DriverRole driverRole, Location location, int permit_number, int status) {
        super(id_driver, name, email, phone, password,Role.CLIENT, location);
        this.id_driver = id_driver;
        this.permit_number = permit_number;
        this.driverRole = driverRole;
        this.status = status;
    }

    public int getId_driver() {
        return id_driver;
    }

    public void setId_driver(int id_driver) {
        this.id_driver = id_driver;
    }

    public int getPermit_number() {
        return permit_number;
    }

    public void setPermit_number(int permit_number) {
        this.permit_number = permit_number;
    }

    public DriverRole getDriverRole() {
        return driverRole;
    }
    
    public void setDriverRole(DriverRole driverRole) {
        this.driverRole = driverRole;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Driver [id_driver=" + id_driver + ", name=" + super.getName() + ", driverRole=" + driverRole + ", status=" + status + "]";
    }
}



