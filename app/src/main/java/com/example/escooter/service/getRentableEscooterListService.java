package com.example.escooter.service;

import android.app.Activity;
import android.content.Context;

import com.example.escooter.R;
import com.example.escooter.network.HttpRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class getRentableEscooterListService {
    String baseUrl = "http://36.232.110.240:8080";
    String userDataUrl = baseUrl + "/api/getRentableEscooterList";
    private Context context;
    public  getRentableEscooterListService(Context context,GoogleMap googleMap , String ownLongitude, String ownfLatitude) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("longitude", ownLongitude);
            postData.put("latitude", ownfLatitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpRequest getRentableEscooterList = new HttpRequest(userDataUrl);
        // 發送 HTTP POST 請求
        getRentableEscooterList.httpPost(postData, result -> {
            try {
                //json檔案資料處理
                JSONArray escooters = result.getJSONArray("escooters");
                for (int i = 0; i < escooters.length(); i++) {
                    JSONObject escooter = escooters.getJSONObject(i);
                    String escooterId = escooter.getString("escooterId");
                    JSONObject gps = escooter.getJSONObject("gps");
                    double latitude = gps.getDouble("latitude");
                    double longitude = gps.getDouble("longitude");

                    //切換為ui線程，才能使用google map的更改
                    ((Activity) context).runOnUiThread(() -> {
                        LatLng Escooter = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(Escooter).title(escooterId).icon(BitmapDescriptorFactory.fromResource(R.drawable.escooter)));
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
