package org.wamiago.wamiago.entities;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private Location location;
    private Gender gender;
    private String profilePicture;
    private boolean isVerified;
    private AccountStatus accountStatus;
    private LocalDate dateOfBirth;
    private Status status;

    public enum Role {
        CLIENT, ADMIN
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum AccountStatus {
        ACTIVE, BANNED, DEACTIVATED
    }

    public enum Status {
        ONLINE, OFFLINE
    }


    public User(int id, String name, String email, String phone, String password, Role role, Location location,
                Gender gender, String profilePicture, boolean isVerified, AccountStatus accountStatus,
                LocalDate dateOfBirth, Status status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.location = location;
        this.gender = gender;
        this.profilePicture = profilePicture;
        this.isVerified = isVerified;
        this.accountStatus = accountStatus;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public User() {
        this.id = 0;
        this.name = "";
        this.email = "";
        this.phone = "";
        this.password = "";
        this.role = Role.CLIENT;
        this.location = new Location();
        this.gender = Gender.MALE;
        this.profilePicture = "";
        this.isVerified = false;
        this.accountStatus = AccountStatus.ACTIVE;
        this.dateOfBirth = null;
        this.status = Status.OFFLINE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", location=" + location +
                ", gender=" + gender +
                ", profilePicture='" + profilePicture + '\'' +
                ", isVerified=" + isVerified +
                ", accountStatus=" + accountStatus +
                ", dateOfBirth=" + dateOfBirth +
                ", status=" + status +
                '}';
    }
}