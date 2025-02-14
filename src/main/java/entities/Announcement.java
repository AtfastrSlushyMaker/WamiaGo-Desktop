package entities;

import java.sql.Timestamp;
import java.util.Objects;

public class Announcement {
    private int idAnnouncement;
    private Driver transporter;
    private String title;
    private String content;
    private Timestamp date;
    private Zone zone;
    private boolean status;

    public enum Zone {
        Ariana, Béja, Ben_Arous, Bizerte, Gabès, Gafsa, Jendouba, Kairouan, Kasserine, Kebili, Kef, Mahdia, Manouba,
        Medenine, Monastir, Nabeul, Sfax, Sidi_Bouzid, Siliana, Sousse, Tataouine, Tozeur, Tunis, Zaghouan
    }

    // Constructeur par défaut
    public Announcement() {
        this(0, new Driver(), "", "", new Timestamp(System.currentTimeMillis()), Zone.Tunis, false);
    }

    // Constructeur avec tous les paramètres
    public Announcement(int idAnnouncement, Driver transporter, String title, String content,
                        Timestamp date, Zone zone, boolean status) {
        this.idAnnouncement = idAnnouncement;
        this.transporter = transporter;
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

    public Driver getTransporter() {
        return transporter;
    }

    public void setTransporter(Driver transporter) {
        this.transporter = transporter;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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
        return idAnnouncement == that.idAnnouncement && status == that.status &&
                Objects.equals(transporter, that.transporter) && Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) && Objects.equals(date, that.date) && zone == that.zone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnnouncement, transporter, title, content, date, zone, status);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "idAnnouncement=" + idAnnouncement +
                ", transporter=" + transporter +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", zone=" + zone +
                ", status=" + status +
                '}';
    }
}