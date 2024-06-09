package com.example.escooter.service;

import static java.lang.Math.round;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.escooter.R;
import com.example.escooter.viewmodel.MapViewModel;
import com.example.escooter.viewmodel.RentViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class EscooterService {
    private ScheduledExecutorService scheduler;
    private RentViewModel rentViewModel;
    private MapViewModel mapViewModel;
    private long startTime;
    private long tempTime;
    private int duration;
    private double feePerMin = 100;
    private int TotalCost;
    private boolean isGet = false;
    private Context context; // 新增 context
    private GoogleMap googleMap; // 假设你在其他地方初始化了 googleMap
    private Polyline currentPolyline;

    public EscooterService(RentViewModel rentViewModel, MapViewModel mapViewModel) {
        startTime = System.currentTimeMillis();
        this.rentViewModel = rentViewModel;
        this.mapViewModel = mapViewModel;
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startGpsUpdates() {
        scheduler.scheduleWithFixedDelay(() -> {

            isGet = true;
            tempTime = System.currentTimeMillis();
            duration = (int) ((tempTime - startTime) / 1000/ 60);
            TotalCost = (int) (duration * feePerMin);
            rentViewModel.getEscooterGps();
            LatLng currentLatLng = mapViewModel.getCurrentLatLng().getValue();
            LatLng markerLatLng = mapViewModel.getMarkerLatLng().getValue();
            // 設置距離閾值 20 米
            float distanceThreshold = 20.0f;
            // 計算當前位置與 marker 的距離
            float[] results = new float[1];
            Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                    markerLatLng.latitude, markerLatLng.longitude, results);
            float distance = results[0];

            // 判斷距離是否超過閾值，如果超過則顯示 marker
            if (distance > distanceThreshold) {
//                getDirections(currentLatLng, markerLatLng);
            } else {
                if (currentPolyline != null) {
                    currentPolyline.remove(); // 移除 polyline
                    currentPolyline = null;
                }
                googleMap.clear(); // 清除地圖上的所有圖層
            }
        }, 0, 1, TimeUnit.SECONDS); // 初始延迟为0，之后每60秒执行一次
    }

    public void stopGpsUpdates() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException ex) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    public String getFormattedTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date(timeInMillis);
        return sdf.format(date);
    }
    public String getStartTime(){
        return getFormattedTime(startTime);
    }
    public int getTotalCost(){
        return TotalCost;
    }
    public int getDuration() {
        return duration;
    }
    public boolean getIsGet() {
        return isGet;
    }

    //     使用Directions API獲取導航路徑
    private void getDirections(LatLng origin, LatLng destination) {

        System.out.println("--------------");
        String apiKey = getApiKey();
        System.out.println(apiKey);
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=bicycling" + // 指定为骑行模式
                "&alternatives=true" + // 請求多條路線
                "&key=" + apiKey;
        System.out.println(url);
        // 發送HTTP請求獲取導航路徑
        new Thread(() -> {
            try {
                URL directionsUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) directionsUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();
                parseDirectionsResponse(response); // 解析導航路徑並顯示在地圖上

                bufferedReader.close();
                streamReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getApiKey() {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            return metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 解析Directions API返回的JSON數據並在地圖上顯示導航路徑
    private void parseDirectionsResponse(String response) {
        // 需要一个 Context，使用 context 代替 requireActivity()
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray routes = jsonResponse.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject bestRoute = selectBestRoute(routes);
                    JSONObject overviewPolyline = bestRoute.getJSONObject("overview_polyline");
                    String encodedPolyline = overviewPolyline.getString("points");
                    List<LatLng> points = decodePolyline(encodedPolyline);

                    // 显示导航路径
                    if (currentPolyline != null) {
                        currentPolyline.remove();
                    }
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(points)
                            .color(0xffD08343)
                            .width(14);
                    System.out.println(polylineOptions);
                    currentPolyline = googleMap.addPolyline(polylineOptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private JSONObject selectBestRoute(JSONArray routes) throws JSONException {
        // 根据需求选择最佳路线，例如最短时间或距离
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

                // 比较逻辑，例如比较总时间
                if (duration < bestRouteDuration) {
                    bestRoute = route;
                }
            }
        }
        return bestRoute;
    }

    // 解碼polyline字符串
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
