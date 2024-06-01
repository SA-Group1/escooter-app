package com.example.escooter.ui.menu;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.EscooterGpsCallback;
import com.example.escooter.callback.ParkCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.ReturnCallback;
import com.example.escooter.callback.UpdataUserCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.data.model.User;
import com.example.escooter.service.RentalService;
import com.example.escooter.service.UserService;
import com.example.escooter.ui.user.UserFormState;
import com.example.escooter.ui.user.UserResult;

import java.util.List;

public class RentViewModel extends ViewModel {
    private final RentalService RentalService = new RentalService();
    private final MutableLiveData<RentResult> rentResult = new MutableLiveData<>();
    private final MutableLiveData<ParkResult> ParkResult = new MutableLiveData<>();
    private final MutableLiveData<ReturnResult> ReturnResult = new MutableLiveData<>();
    private final MutableLiveData<EscooterGpsResult> EscooterGpsResult = new MutableLiveData<>();
    private final MutableLiveData<String> ownLongitude = new MutableLiveData<>();
    private final MutableLiveData<String> ownLatitude = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> escooterId = new MutableLiveData<>();

    public LiveData<RentResult> getRentResult() {
        return rentResult;
    }
    public LiveData<ParkResult> getParkResult() {
        return ParkResult;
    }
    public LiveData<ReturnResult> getReturnResult() {
        return ReturnResult;
    }
    public LiveData<EscooterGpsResult> getEscooterGpsResult() {
        return EscooterGpsResult;
    }
    public LiveData<String> getEscooterId() {
        return escooterId;
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

    public void rentEscooter() {
        RentalService.rentEscooter(account.getValue(), password.getValue(), escooterId.getValue(), new RentalCallback() {

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

    public void updateEscooterParkStatus() {
        RentalService.updateEscooterParkStatus(account.getValue(), password.getValue(), new ParkCallback() {

            @Override
            public void onSuccess(boolean isPark) {
                ParkResult.postValue(new ParkResult(isPark));
            }

            @Override
            public void onFailure(Exception e) {
                rentResult.postValue(new RentResult(e));
            }
        });
    }

    public void returnEscooter() {
        RentalService.returnEscooter(account.getValue(), password.getValue(), new ReturnCallback() {

            @Override
            public void onSuccess(RentalRecord escooter) {
                System.out.println("Success Return");
                ReturnResult.postValue(new ReturnResult(escooter));
            }

            @Override
            public void onFailure(Exception e) {
                ReturnResult.postValue(new ReturnResult(e));
            }
        });
    }

    public void getEscooterGps() {
        RentalService.getEscooterGps(escooterId.getValue(), new EscooterGpsCallback() {

            @Override
            public void onSuccess(Gps gps) {
                System.out.println("Success Get Gps");
                EscooterGpsResult.postValue(new EscooterGpsResult(gps));
            }

            @Override
            public void onFailure(Exception e) {
                EscooterGpsResult.postValue(new EscooterGpsResult(e));
            }
        });
    }

    public void setUserlocation(String ownLongitude,String ownLatitude) {
        this.ownLongitude.setValue(ownLongitude);
        this.ownLatitude.setValue(ownLatitude);
    }

    public void setUserCredential(String account,String password) {
        this.account.setValue(account);
        this.password.setValue(password);
    }

    public void setEscooterId(String escooterId) {
        this.escooterId.setValue(escooterId);
    }

}
