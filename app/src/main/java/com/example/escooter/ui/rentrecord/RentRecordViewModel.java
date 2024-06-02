package com.example.escooter.ui.rentrecord;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.RentRecordCallback;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.service.RentalService;

import java.util.ArrayList;

public class RentRecordViewModel extends ViewModel {
    private final RentalService RentalService = new RentalService();
    private final MutableLiveData<RentRecordResult> RentRecordResult = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();

    public LiveData<RentRecordResult> getRentRecordResult() {
        return RentRecordResult;
    }

    public void getRentalRecordList() {
        RentalService.getRentalRecordList(account.getValue(), password.getValue(), new RentRecordCallback() {

            @Override
            public void onSuccess(ArrayList<RentalRecord> RentalRecordList) {
                System.out.println("Success getRentalRecordList");
                RentRecordResult.postValue(new RentRecordResult(RentalRecordList));
            }

            @Override
            public void onFailure(Exception e) {
                RentRecordResult.postValue(new RentRecordResult(e));
            }
        });
    }

    public void setUserCredential(String account,String password) {
        this.account.setValue(account);
        this.password.setValue(password);
    }

}
