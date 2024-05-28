package com.example.escooter.ui.payment;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.BindCreditCardCallback;
import com.example.escooter.callback.UnbindCreditCardCallback;
import com.example.escooter.callback.UpdataUserCallback;
import com.example.escooter.callback.UserCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.User;
import com.example.escooter.service.CreditCardService;
import com.example.escooter.service.UserService;
import com.example.escooter.ui.user.UserFormState;
import com.example.escooter.ui.user.UserResult;

public class CreditCardViewModel extends ViewModel {
    private final CreditCardService CreditCardService = new CreditCardService();
    private final MutableLiveData<CreditCardFormState> creditCardFormState = new MutableLiveData<>();
    private final MutableLiveData<CreditCardResult> CreditCardResult = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> CardNumber = new MutableLiveData<>();
    private final MutableLiveData<String> VaildThru = new MutableLiveData<>();
    private final MutableLiveData<String> Cvv = new MutableLiveData<>();

    public LiveData<CreditCardResult> getCreditCardResult() {
        return CreditCardResult;
    }

    public LiveData<CreditCardFormState> getCreditCardFormState() {
        return creditCardFormState;
    }

    public void bindCreditCard() {
        CreditCardService.bindCreditCard(account.getValue(), password.getValue(), CardNumber.getValue(), VaildThru.getValue(), username.getValue(), Cvv.getValue(), new BindCreditCardCallback() {
            @Override
            public void onSuccess(boolean isBindCreditCard) {
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber(CardNumber.getValue());
                creditCard.setExpirationDate(VaildThru.getValue());
                CreditCardResult.postValue(new CreditCardResult(creditCard));
            }

            @Override
            public void onFailure(Exception e) {
                CreditCardResult.postValue(new CreditCardResult(e));
            }
        });
    }

    public void unbindCreditCard() {
        CreditCardService.unbindCreditCard(account.getValue(), password.getValue(), new UnbindCreditCardCallback() {
            @Override
            public void onSuccess(boolean isUnbindCreditCard) {
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber("null");
                creditCard.setExpirationDate("null");
                CreditCardResult.postValue(new CreditCardResult(creditCard));
            }

            @Override
            public void onFailure(Exception e) {
                CreditCardResult.postValue(new CreditCardResult(e));
            }
        });
    }

    public void setUserCredential(String account,String password,String username) {
        this.account.setValue(account);
        this.password.setValue(password);
        this.username.setValue(username);
    }

    public void updataDataChanged(String cardNumber, String vaildThru, String cvv) {
        if (isUserNameValid(cardNumber) && isEmailValid(vaildThru) && isPhoneNumberValid(cvv)) {
            this.CardNumber.setValue(cardNumber);
            this.VaildThru.setValue(vaildThru);
            this.Cvv.setValue(cvv);
            creditCardFormState.setValue(new CreditCardFormState(true));
        } else {
            creditCardFormState.setValue(new CreditCardFormState(false));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String cardNumber) {
        return cardNumber != null && cardNumber.trim().length() > 5;
    }

    // A placeholder password validation check
    private boolean isEmailValid(String vaildThru) {
        return vaildThru != null && vaildThru.trim().length() == 4;
    }

    private boolean isPhoneNumberValid(String cvv) {
        return cvv != null && cvv.trim().length() == 3;
    }

}
