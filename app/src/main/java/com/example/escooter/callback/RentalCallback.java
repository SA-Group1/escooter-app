package com.example.escooter.callback;

import com.example.escooter.data.model.Escooter;

import java.util.List;

public interface RentalCallback {
    void onSuccess(List<Escooter> escooterList);
    void onFailure(Exception e);
}
