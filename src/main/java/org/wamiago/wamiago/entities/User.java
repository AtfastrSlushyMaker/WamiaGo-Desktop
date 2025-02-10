package org.wamiago.wamiago.entities;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private int idLocation;
    private String address;

    public User(int id, String name, String email, String phone, String password, Role role, int idLocation, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.idLocation = idLocation;
        this.address = address;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public int getIdLocation() { return idLocation; }
    public void setIdLocation(int idLocation) { this.idLocation = idLocation; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", idLocation=" + idLocation +
                ", address='" + address + '\'' +
                '}';
    }
}
