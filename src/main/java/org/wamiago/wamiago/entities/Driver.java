package org.wamiago.wamiago.entities;

import java.time.LocalDate;

public class Driver extends User {
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

    public Driver(int idDriver, int id, String name, String email, String phone, String password, DriverRole driverRole,
                  Location location, String permitNumber, int driverStatus, Gender gender, String profilePicture,
                  boolean isVerified, AccountStatus accountStatus, LocalDate dateOfBirth, Status userStatus) {
        super(id, name, email, phone, password, Role.CLIENT, location, gender, profilePicture, isVerified, accountStatus, dateOfBirth, userStatus);
        this.idDriver = idDriver;
        this.permitNumber = permitNumber;
        this.driverRole = driverRole;
        this.driverStatus = driverStatus;
    }

    public Driver() {
        super(0, "", "", "", "", Role.CLIENT, new Location(), Gender.MALE, "", false, AccountStatus.ACTIVE, null, Status.OFFLINE);
        this.idDriver = 0;
        this.permitNumber = "";
        this.driverRole = null;
        this.driverStatus = DRIVER_INACTIVE;
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
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", role=" + getRole() +
                ", location=" + getLocation() +
                ", gender=" + getGender() +
                ", profilePicture='" + getProfilePicture() + '\'' +
                ", isVerified=" + isVerified() +
                ", accountStatus=" + getAccountStatus() +
                ", dateOfBirth=" + getDateOfBirth() +
                ", userStatus=" + getStatus() +
                '}';
    }
}