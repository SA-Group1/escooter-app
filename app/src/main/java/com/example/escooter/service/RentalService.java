package com.example.escooter.service;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.EscooterGpsCallback;
import com.example.escooter.callback.RentCallback;
import com.example.escooter.callback.RentRecordCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.ReturnCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.repository.RentalRepository;

import java.util.ArrayList;
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

    public void rentEscooter(String account, String password, String escooterId, RentCallback callback) {
        rentalRepository.rentEscooter(account, password, escooterId, new RentCallback() {
            @Override
            public void onSuccess(Escooter escooter) {
                callback.onSuccess(escooter);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void updateEscooterParkStatus(String account, String password, BooleanCallback callback) {
        rentalRepository.updateEscooterParkStatus(account, password, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isPark) {
                callback.onSuccess(isPark);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void returnEscooter(String account, String password, ReturnCallback callback) {
        rentalRepository.returnEscooter(account, password, new ReturnCallback() {
            @Override
            public void onSuccess(RentalRecord escooter) {
                callback.onSuccess(escooter);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getRentalRecordList(String account, String password, RentRecordCallback callback) {
        rentalRepository.getRentalRecordList(account, password, new RentRecordCallback() {
            @Override
            public void onSuccess(ArrayList<RentalRecord> RentalRecordList) {
                callback.onSuccess(RentalRecordList);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getEscooterGps(String escooterId, EscooterGpsCallback callback) {
        rentalRepository.getEscooterGps(escooterId, new EscooterGpsCallback() {
            @Override
            public void onSuccess(Gps gps) {
                callback.onSuccess(gps);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
