package com.example.escooter.repository;


import com.example.escooter.BuildConfig;
import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.EscooterGpsCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.RentCallback;
import com.example.escooter.callback.RentRecordCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.ReturnCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.Gps;
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

    public void rentEscooter(String account, String password, String escooterId, RentCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
            body.put("escooterId", escooterId);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/rentEscooter", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONObject escooter = result.getJSONObject("data");
                    String escooterId = escooter.getString("escooterId");
                    String modelId = escooter.getString("modelId");
                    String status = escooter.getString("status");
                    int batteryLevel = escooter.getInt("batteryLevel");
                    double feePerMinutes = escooter.getDouble("feePerMinutes");
                    JSONObject gps = escooter.getJSONObject("gps");
                    double latitude = gps.getDouble("latitude");
                    double longitude = gps.getDouble("longitude");

                    Escooter selectedEscooter = new Escooter(escooterId, modelId, status, batteryLevel, feePerMinutes, longitude, latitude);

                    callback.onSuccess(selectedEscooter);
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

    public void updateEscooterParkStatus(String account, String password, BooleanCallback callback) {

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

    public void getRentalRecordList(String account, String password, RentRecordCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getRentalRecordList", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONArray rentalRecordArray = result.getJSONArray("data");
                    ArrayList<RentalRecord> rentalRecordList = new ArrayList<>();

                    for (int i = 0; i < rentalRecordArray.length(); i++) {
                        JSONObject rentalRecordJson = rentalRecordArray.getJSONObject(i);
                        RentalRecord rentalRecord = new RentalRecord();
                        rentalRecord.setRentalRecordId(rentalRecordJson.getInt("rentalRecordId"));
                        rentalRecord.setUserId(rentalRecordJson.getInt("userId"));
                        rentalRecord.setEscooterId(rentalRecordJson.getString("escooterId"));
                        rentalRecord.setStartTime(rentalRecordJson.getString("startTime"));
                        rentalRecord.setEndTime(rentalRecordJson.getString("endTime"));
                        rentalRecord.setPaid(rentalRecordJson.getBoolean("isPaid"));
                        rentalRecord.setModelId(rentalRecordJson.getString("modelId"));
                        rentalRecord.setFeePerMinutes(rentalRecordJson.getDouble("feePerMinutes"));
                        rentalRecord.setDuration(rentalRecordJson.getInt("duration"));
                        rentalRecord.setTotalFee(rentalRecordJson.getDouble("totalFee"));

                        rentalRecordList.add(rentalRecord);
                    }

                    callback.onSuccess(rentalRecordList);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getEscooterGps(String escooterId, EscooterGpsCallback callback) {
        JSONObject body = new JSONObject();
        try {
            body.put("escooterId", escooterId);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getEscooterGps", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONObject EscooterLocation = result.getJSONObject("data");
                    System.out.println(EscooterLocation);
                    Gps gps = new Gps();
                    gps.setLatitude(EscooterLocation.getDouble("latitude"));
                    gps.setLongitude(EscooterLocation.getDouble("longitude"));
                    callback.onSuccess(gps);
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}