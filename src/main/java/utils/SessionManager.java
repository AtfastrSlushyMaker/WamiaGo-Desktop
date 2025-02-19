package utils;

import java.util.UUID;
import entities.User;

public class SessionManager {
    private static SessionManager instance;
    private User user;
    private String sessionToken;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public synchronized void setUser(User user) {
        if (this.user == null) {
            this.user = user;
            regenerateSessionToken();
        }
    }

    public synchronized User getUser() {
        return user;
    }

    public synchronized String getSessionToken() {
        return sessionToken;
    }

    public synchronized void logout() {
        user.setStatus(User.Status.OFFLINE);
        user = null;
        sessionToken = null;
    }


    private void regenerateSessionToken() {
        this.sessionToken = UUID.randomUUID().toString();
    }
}