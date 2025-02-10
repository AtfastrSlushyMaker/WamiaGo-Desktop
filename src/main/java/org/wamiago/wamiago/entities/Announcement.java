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
    private int status;

    // Enum pour la zone
    public enum Zone {
        Ariana, Béja, Ben_Arous, Bizerte, Gabès, Gafsa, Jendouba, Kairouan, Kasserine, Kebili, Kef, Mahdia, Manouba, Medenine, Monastir, Nabeul, Sfax, Sidi_Bouzid, Siliana, Sousse, Tataouine, Tozeur, Tunis, Zaghouan
    }

    public Announcement(int idAnnouncement, int idTransporter, String title, String content, LocalDateTime date, Zone zone, int status) {
        this.idAnnouncement = idAnnouncement;
        this.idTransporter = idTransporter;
        this.title = title;
        this.content = content;
        this.date = date;
        this.zone = zone;
        this.status = status;
    }

    public Announcement() {
        this.idAnnouncement = 0;
        //this.idTransporter = new Driver();
        this.idTransporter = 0;
        this.title = "";
        this.content = "";
        this.date = LocalDateTime.now();
        this.zone = Zone.Tunis;
        this.status = 0;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Announcement)) return false;
        Announcement that = (Announcement) o;
        return getIdAnnouncement() == that.getIdAnnouncement() && getStatus() == that.getStatus() && Objects.equals(getIdTransporter(), that.getIdTransporter()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getContent(), that.getContent()) && Objects.equals(getDate(), that.getDate()) && getZone() == that.getZone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdAnnouncement(), getIdTransporter(), getTitle(), getContent(), getDate(), getZone(), getStatus());
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