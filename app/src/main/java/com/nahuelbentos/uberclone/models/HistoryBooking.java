package com.nahuelbentos.uberclone.models;

public class HistoryBooking {
    private  String idHistoryBooking;
    private  String idClient;
    private  String idDriver;
    private  String origin;
    private  String destination;
    private  String time;
    private  String km;
    private  String status;
    private  double originLat;
    private  double originLng;
    private  double destinationLat;
    private  double destinationLng;

    private  double calificationClient;
    private  double calificationDriver;
    private  long timestamp;


    public HistoryBooking() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HistoryBooking(String idHistoryBooking, String idClient, String idDriver, String origin, String destination, String time, String km, String status, double originLat, double originLng, double destinationLat, double destinationLng) {
        this.idHistoryBooking = idHistoryBooking;
        this.idClient = idClient;
        this.idDriver = idDriver;
        this.origin = origin;
        this.destination = destination;
        this.time = time;
        this.km = km;
        this.status = status;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
    }

    public String getIdHistoryBooking() {
        return idHistoryBooking;
    }

    public void setIdHistoryBooking(String idHistoryBooking) {
        this.idHistoryBooking = idHistoryBooking;
    }

    public double getCalificationClient() {
        return calificationClient;
    }

    public void setCalificationClient(double calificationClient) {
        this.calificationClient = calificationClient;
    }

    public double getCalificationDriver() {
        return calificationDriver;
    }

    public void setCalificationDriver(double calificationDriver) {
        this.calificationDriver = calificationDriver;
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

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
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
