package com.example.escooter.ui.menu;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.UpdataUserCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.User;
import com.example.escooter.service.RentalService;
import com.example.escooter.service.UserService;
import com.example.escooter.ui.user.UserFormState;
import com.example.escooter.ui.user.UserResult;

import java.util.List;

public class RentViewModel extends ViewModel {
    private final RentalService RentalService = new RentalService();
    private final MutableLiveData<RentResult> rentResult = new MutableLiveData<>();
    private final MutableLiveData<String> ownLongitude = new MutableLiveData<>();
    private final MutableLiveData<String> ownLatitude = new MutableLiveData<>();

    public LiveData<RentResult> getRentResult() {
        return rentResult;
    }
    public LiveData<String> getOwnLongitude() {
        return ownLongitude;
    }
    public LiveData<String> getOwnfLatitude() {
        return ownLatitude;
    }

    public void getRentableEscooterList() {
        RentalService.getRentableEscooterList(ownLongitude.getValue(), ownLatitude.getValue(), new RentalCallback() {

            @Override
            public void onSuccess(List<Escooter> escooterList) {
                rentResult.postValue(new RentResult(escooterList));
            }

            @Override
            public void onFailure(Exception e) {
                rentResult.postValue(new RentResult(e));
            }
        });
    }

    public void setUserlocation(String ownLongitude,String ownLatitude) {
        this.ownLongitude.setValue(ownLongitude);
        this.ownLatitude.setValue(ownLatitude);
    }

}
