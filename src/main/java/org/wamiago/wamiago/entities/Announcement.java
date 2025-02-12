package org.wamiago.wamiago.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Announcement {
    private int idAnnouncement;
    private int idTransporter;
    private String title;
    private String content;
    private LocalDateTime date;
    private Zone zone;
    private boolean status;

    public enum Zone {
        Ariana, Béja, Ben_Arous, Bizerte, Gabès, Gafsa, Jendouba, Kairouan, Kasserine, Kebili, Kef, Mahdia, Manouba,
        Medenine, Monastir, Nabeul, Sfax, Sidi_Bouzid, Siliana, Sousse, Tataouine, Tozeur, Tunis, Zaghouan
    }

    public Announcement() {
        this(0, 0, "", "", LocalDateTime.now(), Zone.Tunis, false);
    }

    public Announcement(int idAnnouncement, int idTransporter, String title, String content,
                        LocalDateTime date, Zone zone, boolean status) {
        this.idAnnouncement = idAnnouncement;
        this.idTransporter = idTransporter;
        this.title = title;
        this.content = content;
        this.date = date;
        this.zone = zone;
        this.status = status;
    }

    // Getters and Setters
    public int getIdAnnouncement() {
        return idAnnouncement;
    }

    public void setIdAnnouncement(int idAnnouncement) {
        this.idAnnouncement = idAnnouncement;
    }

    public int getIdTransporter() {
        return idTransporter;
    }

    public void setIdTransporter(int idTransporter) {
        this.idTransporter = idTransporter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement)) return false;
        Announcement that = (Announcement) o;
        return idAnnouncement == that.idAnnouncement && idTransporter == that.idTransporter && status == that.status &&
                Objects.equals(title, that.title) && Objects.equals(content, that.content) &&
                Objects.equals(date, that.date) && zone == that.zone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnnouncement, idTransporter, title, content, date, zone, status);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "idAnnouncement=" + idAnnouncement +
                ", idTransporter=" + idTransporter +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", zone=" + zone +
                ", status=" + status +
                '}';
    }
}
