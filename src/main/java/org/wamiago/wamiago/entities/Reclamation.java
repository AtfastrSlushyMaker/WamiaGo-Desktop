package org.wamiago.wamiago.entities;

import java.sql.Timestamp;
import java.util.Objects;

public class Reclamation {
    private int idReclamation;
    private User user;
    private String content;
    private Timestamp date;
    private int status;

    public Reclamation(int idReclamation, User user, String content, Timestamp date, int status) {
        this.idReclamation = idReclamation;
        this.user = user;
        this.content = content;
        this.date = date;
        this.status = status;
    }

    public Reclamation() {
        this.idReclamation = 0;
        this.user = new User();
        this.content = "";
        this.date = new Timestamp(System.currentTimeMillis());
        this.status = 0;

    }

    public Reclamation(User user, String content, Timestamp date, int status) {
        this.user = user;
        this.content = content;
        this.date = date;
        this.status = status;
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getStatus() {
        return status;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", user=" + user +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reclamation that)) return false;
        return idReclamation == that.idReclamation && status == that.status && Objects.equals(user, that.user) && Objects.equals(content, that.content) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReclamation, user, content, date, status);
    }
}
