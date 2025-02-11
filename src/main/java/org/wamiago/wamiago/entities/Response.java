package org.wamiago.wamiago.entities;

import java.sql.Date;
import java.sql.Timestamp;

public class Response {
    private int id_response;
    private int id_reclamation;
    private String content;
    private Timestamp date;


public Response() {}

    public Response(int id_response, int id_reclamation, String content, Timestamp date) {
    this.id_response = id_response;
    this.id_reclamation = id_reclamation;
    this.content = content;
    this.date = date;

    }
    public Response(int id_reclamation, String content, Timestamp date) {
        this.id_response = id_response;
        this.id_reclamation = id_reclamation;
        this.content = content;
        this.date = date;

    }

    public int getId_response() {
    return id_response;
    }
    public void setId_response(int id_response) {
    this.id_response = id_response;
    }
    public int getId_reclamation() {
    return id_reclamation;
    }
    public void setId_reclamation(int id_reclamation) {
    this.id_reclamation = id_reclamation;
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
                ", id_reclamation=" + id_reclamation +
                ", content='" + content + '\'' +
                ", date=" + date +
                '}';
    }
}
