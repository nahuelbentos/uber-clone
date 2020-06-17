package com.nahuelbentos.uberclone.models;

public class ClientBooking {
    private  String idClient;
    private  String idDriver;
    private  String origin;
    private  String destination;
    private  String time;
    private  String km;
    private  String status;
    private  double originLat;
    private  double originLlng;
    private  double destinationLat;
    private  double destinationLng;

    public ClientBooking(String idClient, String idDriver, String origin, String destination, String time, String km, String status, double originLat, double originLlng, double destinationLat, double destinationLng) {
        this.idClient = idClient;
        this.idDriver = idDriver;
        this.origin = origin;
        this.destination = destination;
        this.time = time;
        this.km = km;
        this.status = status;
        this.originLat = originLat;
        this.originLlng = originLlng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLlng() {
        return originLlng;
    }

    public void setOriginLlng(double originLlng) {
        this.originLlng = originLlng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }
}
