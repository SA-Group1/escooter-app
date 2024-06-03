package com.example.escooter.service;

import com.example.escooter.callback.ReturnAreasCallback;
import com.example.escooter.data.model.ReturnAreas;
import com.example.escooter.repository.MapRepository;

public class MapService {
    private final MapRepository mapRepository = new MapRepository();
    public void getReturnAreas(ReturnAreasCallback callback) {
        mapRepository.getReturnAreas(new ReturnAreasCallback() {
            @Override
            public void onSuccess(ReturnAreas returnArea) {
                callback.onSuccess(returnArea);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
