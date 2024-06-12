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
import com.example.escooter.callback.DirectionCallback;
import com.example.escooter.callback.HttpResultCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.repository.EscooterRepository;
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
    private EscooterRepository escooterRepository;
    private long startTime;
    private long tempTime;
    private int duration;
    private double feePerMin = 0.3;
    private int totalCost;
    private boolean isGet = false;

    public EscooterService(Context context) {
        this.escooterRepository = new EscooterRepository(context);
        startTime = System.currentTimeMillis();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startGpsUpdates(RentViewModel rentViewModel, MapViewModel mapViewModel) {
        scheduler.scheduleWithFixedDelay(() -> {
            isGet = true;
            tempTime = System.currentTimeMillis();
            duration = (int) ((tempTime - startTime) / 1000 );
            totalCost = (int) (duration * feePerMin);
            rentViewModel.getEscooterGps();
            LatLng currentLatLng = mapViewModel.getCurrentLatLng().getValue();
            LatLng markerLatLng = mapViewModel.getMarkerLatLng().getValue();

            float distanceThreshold = 20.0f;
            float[] results = new float[1];
            Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                    markerLatLng.latitude, markerLatLng.longitude, results);
            float distance = results[0];

            if (distance > distanceThreshold) {
                //導引線尚未開啟
//                getDirections(currentLatLng, markerLatLng, mapViewModel);
            } else {
                mapViewModel.setPolylinePoints(null);
            }
        }, 0, 3, TimeUnit.SECONDS);
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

    private void getDirections(LatLng origin, LatLng destination, MapViewModel mapViewModel) {
        escooterRepository.fetchDirections(origin, destination, new DirectionCallback() {
            @Override
            public void onSuccess(List<LatLng> points) {
                new Handler(Looper.getMainLooper()).post(() -> mapViewModel.setPolylinePoints(points));
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println(e);
            }
        });
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
        return totalCost;
    }
    public int getDuration() {
        return duration;
    }
    public boolean getIsGet() {
        return isGet;
    }
    public void resetStartTime() {
        startTime = System.currentTimeMillis();
    }


}
