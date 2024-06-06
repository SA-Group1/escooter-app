package com.example.escooter.service;

import static java.lang.Math.round;

import com.example.escooter.viewmodel.RentViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class EscooterService {
    private ScheduledExecutorService scheduler;
    private RentViewModel rentViewModel;
    private long startTime;
    private long tempTime;
    private int duration;
    private double feePerMin = 100;
    private int TotalCost;
    private boolean isGet = false;

    public EscooterService(RentViewModel rentViewModel) {
        startTime = System.currentTimeMillis();
        this.rentViewModel = rentViewModel;
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startGpsUpdates() {
        scheduler.scheduleWithFixedDelay(() -> {
            isGet = true;
            tempTime = System.currentTimeMillis();
            duration = (int) ((tempTime - startTime) / 1000/ 60);
            TotalCost = (int) (duration * feePerMin);
            rentViewModel.getEscooterGps();

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
}
