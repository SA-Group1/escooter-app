package com.example.escooter.service;

import com.example.escooter.callback.RentalCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.repository.RentalRepository;

import java.util.List;

public class RentalService {
    private final RentalRepository rentalRepository = new RentalRepository();
    public void getRentableEscooterList(String ownLongitude, String ownfLatitude, RentalCallback callback) {
        rentalRepository.getRentableEscooterList(ownLongitude, ownfLatitude, new RentalCallback() {
            @Override
            public void onSuccess(List<Escooter> escooterList) {
                callback.onSuccess(escooterList);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
