package entities;

import java.util.Date;

public class Request {
    private Request request;
    private int idRequest;
    private String idClient;
    private String idTaxi;
    private String idDepartureLocation;
    private String idArrivalLocation;
    private String status;
    private Date requestDate;
    private String clientName;
    private String driverName;

    public Request(int idRequest, String idClient, String idTaxi, String idDepartureLocation,
                   String idArrivalLocation, String status, Date requestDate, String clientName, String driverName) {
        this.idRequest = idRequest;
        this.idClient = idClient;
        this.idTaxi = idTaxi;
        this.idDepartureLocation = idDepartureLocation;
        this.idArrivalLocation = idArrivalLocation;
        this.status = status;
        this.requestDate = requestDate;
        this.clientName = clientName;
        this.driverName = driverName;
    }

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getIdTaxi() {
        return idTaxi;
    }

    public void setIdTaxi(String idTaxi) {
        this.idTaxi = idTaxi;
    }

    public String getIdDepartureLocation() {
        return idDepartureLocation;
    }

    public void setIdDepartureLocation(String idDepartureLocation) {
        this.idDepartureLocation = idDepartureLocation;
    }

    public String getIdArrivalLocation() {
        return idArrivalLocation;
    }

    public void setIdArrivalLocation(String idArrivalLocation) {
        this.idArrivalLocation = idArrivalLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
