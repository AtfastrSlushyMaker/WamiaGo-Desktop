package org.wamiago.wamiago.entities;

public enum Role {
    CLIENT("client"),
    ADMIN("admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String roleStr) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleStr);
    }

    @Override
    public String toString() {
        return value;
    }
}
