package org.wamiago.wamiago.entities;

import java.sql.Timestamp;

public class Request {
    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED, CANCELED
    }

    private int idRequest;
    private int idClient;
    private int idTaxi;
    private int idDepartureLocation;
    private int idArrivalLocation;
    private RequestStatus status;
    private Timestamp requestDate;

    public Request() {
    }

    public Request(int idClient, int idTaxi, int idDepartureLocation,
                   int idArrivalLocation, RequestStatus status, Timestamp requestDate) {
        this.idClient = idClient;
        this.idTaxi = idTaxi;
        this.idDepartureLocation = idDepartureLocation;
        this.idArrivalLocation = idArrivalLocation;
        this.status = status;
        this.requestDate = requestDate;
    }

    public Request(int idRequest, int idClient, int idTaxi, int idDepartureLocation,
                   int idArrivalLocation, RequestStatus status, Timestamp requestDate) {
        this.idRequest = idRequest;
        this.idClient = idClient;
        this.idTaxi = idTaxi;
        this.idDepartureLocation = idDepartureLocation;
        this.idArrivalLocation = idArrivalLocation;
        this.status = status;
        this.requestDate = requestDate;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdTaxi() {
        return idTaxi;
    }

    public void setIdTaxi(int idTaxi) {
        this.idTaxi = idTaxi;
    }

    public int getIdDepartureLocation() {
        return idDepartureLocation;
    }

    public void setIdDepartureLocation(int idDepartureLocation) {
        this.idDepartureLocation = idDepartureLocation;
    }

    public int getIdArrivalLocation() {
        return idArrivalLocation;
    }

    public void setIdArrivalLocation(int idArrivalLocation) {
        this.idArrivalLocation = idArrivalLocation;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }
}
