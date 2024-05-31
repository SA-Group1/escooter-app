package com.example.escooter.data.model;

public class RentalRecord {
    private int rentalRecordId;
    private int userId;
    private String escooterId;
    private String startTime;
    private String endTime;
    private boolean isPaid;
    private String modelId;
    private double feePerMinutes;
    private int duration;
    private double totalFee;

    public int getRentalRecordId() {
        return rentalRecordId;
    }

    public void setRentalRecordId(int rentalRecordId) {
        this.rentalRecordId = rentalRecordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEscooterId() {
        return escooterId;
    }

    public void setEscooterId(String escooterId) {
        this.escooterId = escooterId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public double getFeePerMinutes() {
        return feePerMinutes;
    }

    public void setFeePerMinutes(double feePerMinutes) {
        this.feePerMinutes = feePerMinutes;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }
}
