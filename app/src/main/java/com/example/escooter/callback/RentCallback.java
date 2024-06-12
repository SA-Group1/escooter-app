package com.example.escooter.callback;

import com.example.escooter.data.model.Escooter;

import java.util.List;

public interface RentCallback {
    void onSuccess(Escooter escooter);
    void onFailure(Exception e);
}
