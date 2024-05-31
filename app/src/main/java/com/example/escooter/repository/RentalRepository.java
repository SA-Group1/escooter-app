package com.example.escooter.repository;


import com.example.escooter.BuildConfig;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.ParkCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.ReturnCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RentalRepository {
    public void getRentableEscooterList(String ownLongitude, String ownfLatitude, RentalCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("longitude", ownLongitude);
            body.put("latitude", ownfLatitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getRentableEscooterList", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONArray escootersArray = result.getJSONArray("data");
                    List<Escooter> escooterList = new ArrayList<>();

                    for (int i = 0; i < escootersArray.length(); i++) {
                        JSONObject escooterJson = escootersArray.getJSONObject(i);
                        String escooterId = escooterJson.getString("escooterId");
                        String modelId = escooterJson.getString("modelId");
                        String status = escooterJson.getString("status");
                        int batteryLevel = escooterJson.getInt("batteryLevel");
                        double feePerMinutes = escooterJson.getDouble("feePerMinutes");
                        JSONObject gps = escooterJson.getJSONObject("gps");
                        double latitude = gps.getDouble("latitude");
                        double longitude = gps.getDouble("longitude");

                        Escooter escooter = new Escooter(escooterId, modelId, status, batteryLevel, feePerMinutes, longitude, latitude);
                        escooterList.add(escooter);
                    }

                    callback.onSuccess(escooterList);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }

    public void rentEscooter(String account, String password, String escooterId, RentalCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
            body.put("escooterId", escooterId);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        System.out.println(body);
        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/rentEscooter", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    System.out.println(result);
                    JSONObject escooter = result.getJSONObject("data");
                    System.out.println(escooter);
                    String escooterId = escooter.getString("escooterId");
                    String modelId = escooter.getString("modelId");
                    String status = escooter.getString("status");
                    int batteryLevel = escooter.getInt("batteryLevel");
                    double feePerMinutes = escooter.getDouble("feePerMinutes");
                    JSONObject gps = escooter.getJSONObject("gps");
                    double latitude = gps.getDouble("latitude");
                    double longitude = gps.getDouble("longitude");

                    Escooter selectedEscooter = new Escooter(escooterId, modelId, status, batteryLevel, feePerMinutes, longitude, latitude);

                    List<Escooter> escooterList = new ArrayList<>();
                    escooterList.add(selectedEscooter);

                    callback.onSuccess(escooterList);
                } catch (JSONException e) {
                    System.out.println(e);
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }

    public void updateEscooterParkStatus(String account, String password, ParkCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/updateEscooterParkStatus", "PUT", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    boolean isPark = result.getBoolean("status");
                    callback.onSuccess(isPark);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }

    public void returnEscooter(String account, String password, ReturnCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/returnEscooter", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    //寫到這邊
                    RentalRecord rentalRecord = new RentalRecord();
                    JSONObject selectedescooter = result.getJSONObject("data");
                    rentalRecord.setRentalRecordId(selectedescooter.getInt("rentalRecordId"));
                    rentalRecord.setUserId(selectedescooter.getInt("userId"));
                    rentalRecord.setEscooterId(selectedescooter.getString("escooterId"));
                    rentalRecord.setStartTime(selectedescooter.getString("startTime"));
                    rentalRecord.setEndTime(selectedescooter.getString("endTime"));
                    rentalRecord.setPaid(selectedescooter.getBoolean("isPaid"));
                    rentalRecord.setModelId(selectedescooter.getString("modelId"));
                    rentalRecord.setFeePerMinutes(selectedescooter.getDouble("feePerMinutes"));
                    rentalRecord.setDuration(selectedescooter.getInt("duration"));
                    rentalRecord.setTotalFee(selectedescooter.getDouble("totalFee"));

                    callback.onSuccess(rentalRecord);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
            @Override
            public void onError(Exception e) {
                System.out.println(e.toString());
                callback.onFailure(e);
            }
        });
    }
}
