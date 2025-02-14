package org.wamiago.wamiago.entities;

import java.sql.Timestamp;

public class Response {
    private int id_response;
    private Reclamation reclamation;
    private String content;
    private Timestamp date;

    public Response() {
        this.id_response = 0;
        this.reclamation = null;
        this.content = "";
        this.date = new Timestamp(System.currentTimeMillis());
    }

    public Response(int id_response, Reclamation reclamation, String content, Timestamp date) {
        this.id_response = id_response;
        this.reclamation = reclamation;
        this.content = content;
        this.date = date;
    }

    public Response(Reclamation reclamation, String content, Timestamp date) {
        this.reclamation = reclamation;
        this.content = content;
        this.date = date;
    }

    public int getId_response() {
        return id_response;
    }

    public void setId_response(int id_response) {
        this.id_response = id_response;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
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

    @Override
    public String toString() {
        return "Response{" +
                "id_response=" + id_response +
                ", reclamation=" + (reclamation != null ? reclamation.getIdReclamation() : "null") +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
