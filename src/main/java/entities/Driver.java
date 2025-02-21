package entities;

public class Driver{
    User user;
    private int idDriver;
    private String permitNumber;
    private DriverRole driverRole;
    private int driverStatus;

    public static final int DRIVER_INACTIVE = 0;
    public static final int DRIVER_ACTIVE = 1;

    public enum DriverRole {
        TAXI_DRIVER,
        TRANSPORTER,
        CARPOOL_DRIVER
    }

    public Driver(User user,int idDriver,String permitNumber, DriverRole driverRole,int driverStatus) {
        this.user= user;
        this.idDriver = idDriver;
        this.permitNumber = permitNumber;
        this.driverRole = driverRole;
        this.driverStatus = driverStatus;
    }

    public Driver() {
        this.user = new User();
        this.idDriver = 0;
        this.permitNumber = "";
        this.driverRole = null;
        this.driverStatus = DRIVER_INACTIVE;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(int idDriver) {
        this.idDriver = idDriver;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public DriverRole getDriverRole() {
        return driverRole;
    }

    public void setDriverRole(DriverRole driverRole) {
        this.driverRole = driverRole;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        if (driverStatus != DRIVER_INACTIVE && driverStatus != DRIVER_ACTIVE) {
            throw new IllegalArgumentException("Invalid driver status. Must be 0 (INACTIVE) or 1 (ACTIVE).");
        }
        this.driverStatus = driverStatus;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "idDriver=" + idDriver +
                ", permitNumber='" + permitNumber + '\'' +
                ", driverRole=" + driverRole +
                ", driverStatus=" + driverStatus +
                '}';
    }
}