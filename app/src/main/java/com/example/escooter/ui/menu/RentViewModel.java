package com.example.escooter.ui.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.EscooterGpsCallback;
import com.example.escooter.callback.ParkCallback;
import com.example.escooter.callback.RentalCallback;
import com.example.escooter.callback.ReturnCallback;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.service.RentalService;

import java.util.List;

public class RentViewModel extends ViewModel {
    private final RentalService RentalService = new RentalService();
    private final MutableLiveData<RentResult> rentResult = new MutableLiveData<>();
    private final MutableLiveData<ParkResult> ParkResult = new MutableLiveData<>();
    private MutableLiveData<ReturnResult> returnResult = new MutableLiveData<>();
    private final MutableLiveData<EscooterGpsResult> escooterGpsResult = new MutableLiveData<>();
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
        return returnResult;
    }
    public LiveData<EscooterGpsResult> getEscooterGpsResult() {
        return escooterGpsResult;
    }
    public LiveData<String> getEscooterId() {
        return escooterId;
    }

    public void clearReturnResult(){
        returnResult = new MutableLiveData<>();
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
                returnResult.postValue(new ReturnResult(escooter));
            }

            @Override
            public void onFailure(Exception e) {
                returnResult.postValue(new ReturnResult(e));
            }
        });
    }

    public void getEscooterGps() {
        RentalService.getEscooterGps(escooterId.getValue(), new EscooterGpsCallback() {

            @Override
            public void onSuccess(Gps gps) {
                System.out.println("Success Get Gps");
                escooterGpsResult.postValue(new EscooterGpsResult(gps));
            }

            @Override
            public void onFailure(Exception e) {
                escooterGpsResult.postValue(new EscooterGpsResult(e));
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