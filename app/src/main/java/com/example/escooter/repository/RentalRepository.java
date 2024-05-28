package com.example.escooter.repository;


import com.example.escooter.BuildConfig;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.Escooter;
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
}
