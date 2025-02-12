package org.wamiago.wamiago.entities;

public class Driver extends User {
    private int id_driver;
    private String permit_number;
    private DriverRole driverRole;
    private int status;

    public enum DriverRole {
        TAXI_DRIVER,
        TRANSPORTER,
        CARPOOL_DRIVER
    }

    public Driver(int id_driver, int id, String name, String email, String phone, String password, DriverRole driverRole, Location location, String permit_number, int status) {
        super(id, name, email, phone, password, Role.CLIENT, location);
        this.id_driver = id_driver;
        this.permit_number = permit_number;
        this.driverRole = driverRole;
        this.status = status;
    }

    public Driver() {
        super(0, "", "", "", "", Role.CLIENT, new Location());
        this.id_driver = 0;
        this.permit_number = "";
        this.driverRole = null;
        this.status = 0;
    }

    public int getId_driver() {
        return id_driver;
    }

    public void setId_driver(int id_driver) {
        this.id_driver = id_driver;
    }

    public String getPermit_number() {
        return permit_number;
    }

    public void setPermit_number(String permit_number) {
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
        return "Driver [id_driver=" + id_driver + ", id=" + super.getId() + ", name=" + super.getName() + ", driverRole=" + driverRole + ", status=" + status + "]";
    }
}