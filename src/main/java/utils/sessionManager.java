package utils;

import entities.User;

public class sessionManager {
    private static sessionManager instance;
    private User loggedInUser;

    private sessionManager() {}

    public static sessionManager getInstance() {
        if (instance == null) {
            instance = new sessionManager();
        }
        return instance;
    }

    public void setUser(User user) {
        this.loggedInUser = user;
    }

    public User getUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }

    public boolean isAuthenticated() {
        return loggedInUser != null;
    }
}
