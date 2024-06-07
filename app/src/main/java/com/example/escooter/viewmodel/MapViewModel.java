package com.example.escooter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.ReturnAreasCallback;
import com.example.escooter.data.model.ReturnAreas;
import com.example.escooter.service.MapService;
import com.example.escooter.ui.menu.MapResult;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MapViewModel extends ViewModel {
    private final MapService MapService = new MapService();
    private final MutableLiveData<MapResult> mapResult = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentLatLng = new MutableLiveData<>();
    private final MutableLiveData<LatLng> markerLatLng = new MutableLiveData<>();
    public LiveData<MapResult> getMapResult() {
        return mapResult;
    }
    public LiveData<LatLng> getCurrentLatLng() {
        return currentLatLng;
    }
    public LiveData<LatLng> getMarkerLatLng() {
        return markerLatLng;
    }

    public void getReturnAreas() {
        MapService.getReturnAreas(new ReturnAreasCallback() {

            @Override
            public void onSuccess(ReturnAreas returnArea) {
                System.out.println("Success Get Return Area");
                mapResult.postValue(new MapResult(returnArea));
            }

            @Override
            public void onFailure(Exception e) {
                mapResult.postValue(new MapResult(e));
            }
        });
    }

    public void setDirections(LatLng currentLatLng,LatLng markerLatLng) {
        this.currentLatLng.setValue(currentLatLng);
        this.markerLatLng.setValue(markerLatLng);
    }
}
