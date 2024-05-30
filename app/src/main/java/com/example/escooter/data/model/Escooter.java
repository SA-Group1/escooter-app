package com.example.escooter.data.model;

public class Escooter {
    private String escooterId;
    private String modelId;
    private String status;
    private int batteryLevel;
    private double feePerMinutes;
    private double longitude;
    private double latitude;

    public Escooter(String escooterId, String modelId, String status, int batteryLevel, double feePerMinutes, double longitude, double latitude) {
        this.escooterId = escooterId;
        this.modelId = modelId;
        this.status = status;
        this.batteryLevel = batteryLevel;
        this.feePerMinutes = feePerMinutes;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and setters
    public String getEscooterId() {
        return escooterId;
    }

    public void setEscooterId(String escooterId) {
        this.escooterId = escooterId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public double getFeePerMinutes() {
        return feePerMinutes;
    }

    public void setFeePerMinutes(double feePerMinutes) {
        this.feePerMinutes = feePerMinutes;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

