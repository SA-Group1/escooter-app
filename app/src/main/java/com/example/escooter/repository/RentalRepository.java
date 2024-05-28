package com.example.escooter.repository;

import android.app.Activity;

import com.example.escooter.BuildConfig;
import com.example.escooter.R;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.network.HttpRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RentalRepository {
    public void getRentableEscooterList(String account, String password, UserCallback callback) {

        JSONObject body = new JSONObject();
        try {
            body.put("account", account);
            body.put("password", password);
        } catch (JSONException e) {
            callback.onFailure(e);
            return;
        }

        HttpRequest.httpRequest(BuildConfig.BASE_URL + "/getRentableEscooterList", "POST", body, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    JSONArray escooters = result.getJSONArray("data");
                    for (int i = 0; i < escooters.length(); i++) {
                        JSONObject escooter = escooters.getJSONObject(i);
                        String escooterId = escooter.getString("escooterId");
                        JSONObject gps = escooter.getJSONObject("gps");
                        double latitude = gps.getDouble("latitude");
                        double longitude = gps.getDouble("longitude");

                        //切換為ui線程，才能使用google map的更改
//                        ((Activity) context).runOnUiThread(() -> {
//                            LatLng Escooter = new LatLng(latitude, longitude);
//                            googleMap.addMarker(new MarkerOptions().position(Escooter).title(escooterId).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_escooter)));
//                        });
                    }

//                    callback.onSuccess();
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
