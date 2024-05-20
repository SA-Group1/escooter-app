package com.example.escooter.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RentalRecord {
    // 根據你的需要填寫 RentalRecord 的屬性和方法
    private String escooterModel;
    private String rentRecordId;
    private String escooterId;
    private String escooterRentTime;
    private String escooterReturnTime;
    private double feePerMin;
    private int duration;
    private int totalFee;

    // Getter and Setter for escooterModel
    public String getEscooterModel() {
        return escooterModel;
    }

    public void setEscooterModel(String escooterModel) {
        this.escooterModel = escooterModel;
    }

    // Getter and Setter for rentRecordId
    public String getRentRecordId() {
        return rentRecordId;
    }

    public void setRentRecordId(String rentRecordId) {
        this.rentRecordId = rentRecordId;
    }

    // Getter and Setter for escooterId
    public String getEscooterId() {
        return escooterId;
    }

    public void setEscooterId(String escooterId) {
        this.escooterId = escooterId;
    }

    // Getter and Setter for escooterRentTime
    public String getEscooterRentTime() {
        return escooterRentTime;
    }

    public void setEscooterRentTime(String escooterRentTime) {
        this.escooterRentTime = escooterRentTime;
    }

    // Getter and Setter for escooterReturnTime
    public String getEscooterReturnTime() {
        return escooterReturnTime;
    }

    public void setEscooterReturnTime(String escooterReturnTime) {
        this.escooterReturnTime = escooterReturnTime;
    }

    // Getter and Setter for feePerMin
    public double getFeePerMin() {
        return feePerMin;
    }

    public void setFeePerMin(double feePerMin) {
        this.feePerMin = feePerMin;
    }

    // Getter and Setter for rentingTime
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Getter and Setter for totalFee
    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public static RentalRecord fromJson(JSONObject jsonObject) throws JSONException {
        // 根據 JSON 解析並返回 RentalRecord 實例
        return new RentalRecord();
    }
}
