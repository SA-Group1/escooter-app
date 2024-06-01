package com.example.escooter.service;

import com.example.escooter.ui.menu.RentViewModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class EscooterService {
    private ScheduledExecutorService scheduler;
    private RentViewModel rentViewModel;

    public EscooterService(RentViewModel rentViewModel) {
        this.rentViewModel = rentViewModel;
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startGpsUpdates() {
        scheduler.scheduleWithFixedDelay(() -> {
            rentViewModel.getEscooterGps();
        }, 0, 60, TimeUnit.SECONDS); // 初始延迟为0，之后每60秒执行一次
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

}
