package com.example.escooter.repository;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.escooter.BuildConfig;
import com.example.escooter.callback.DirectionCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.network.HttpRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EscooterRepository {
    private Context context;

    public EscooterRepository(Context context) {
        this.context = context;
    }

    public void fetchDirections(LatLng origin, LatLng destination, DirectionCallback callback) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onFailure(new Exception("API key is missing"));
            return;
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=bicycling" + // 指定为骑行模式
                "&alternatives=true" + // 请求多条路线
                "&key=" + apiKey;

        HttpRequest.httpRequest(url, "GET", null, new HttpResultCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    List<LatLng> points = parseDirectionsResponse(result);
                    System.out.println(points);
                    callback.onSuccess(points);
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

    private String getApiKey() {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            if (metaData != null) {
                return metaData.getString("com.google.android.geo.API_KEY");
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<LatLng> parseDirectionsResponse(JSONObject response) throws JSONException {
        List<LatLng> poly = new ArrayList<>();
        JSONArray routes = response.getJSONArray("routes");
        if (routes.length() > 0) {
            JSONObject bestRoute = selectBestRoute(routes);
            JSONObject overviewPolyline = bestRoute.getJSONObject("overview_polyline");
            String encodedPolyline = overviewPolyline.getString("points");
            poly = decodePolyline(encodedPolyline);
        }
        return poly;
    }

    private JSONObject selectBestRoute(JSONArray routes) throws JSONException {
        JSONObject bestRoute = routes.getJSONObject(0);
        for (int i = 1; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            JSONArray legs = route.getJSONArray("legs");
            JSONArray bestRouteLegs = bestRoute.getJSONArray("legs");

            if (legs.length() > 0 && bestRouteLegs.length() > 0) {
                JSONObject leg = legs.getJSONObject(0);
                JSONObject bestRouteLeg = bestRouteLegs.getJSONObject(0);

                int duration = leg.getJSONObject("duration").getInt("value");
                int bestRouteDuration = bestRouteLeg.getJSONObject("duration").getInt("value");

                if (duration < bestRouteDuration) {
                    bestRoute = route;
                }
            }
        }
        return bestRoute;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
