package com.example.escooter.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.UserPaymentCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
import com.example.escooter.service.CreditCardService;
import com.example.escooter.ui.payment.CreditCardFormState;
import com.example.escooter.ui.payment.UserPaymentResult;

public class CreditCardViewModel extends ViewModel {
    private final CreditCardService CreditCardService = new CreditCardService();
    private final MutableLiveData<CreditCardFormState> creditCardFormState = new MutableLiveData<>();
    private final MutableLiveData<UserPaymentResult> userPaymentResult = new MutableLiveData<>();
    private final MutableLiveData<String> account = new MutableLiveData<>();
    private final MutableLiveData<String> password = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> CardNumber = new MutableLiveData<>();
    private final MutableLiveData<String> VaildThru = new MutableLiveData<>();
    private final MutableLiveData<String> Cvv = new MutableLiveData<>();

    public LiveData<UserPaymentResult> getUserPaymentResult() {
        return userPaymentResult;
    }

    public LiveData<CreditCardFormState> getCreditCardFormState() {
        return creditCardFormState;
    }

    public void bindCreditCard() {
        CreditCardService.bindCreditCard(account.getValue(), password.getValue(), CardNumber.getValue(), VaildThru.getValue(), username.getValue(), Cvv.getValue(), new BooleanCallback() {
            @Override
            public void onSuccess(boolean isBindCreditCard) {
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber(CardNumber.getValue());
                creditCard.setExpirationDate(VaildThru.getValue());
                userPaymentResult.postValue(new UserPaymentResult(creditCard,null));
            }

            @Override
            public void onFailure(Exception e) {
                userPaymentResult.postValue(new UserPaymentResult(e));
            }
        });
    }

    public void unbindCreditCard() {
        CreditCardService.unbindCreditCard(account.getValue(), password.getValue(), new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUnbindCreditCard) {
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber("null");
                creditCard.setExpirationDate("null");
                userPaymentResult.postValue(new UserPaymentResult(creditCard,null));
            }

            @Override
            public void onFailure(Exception e) {
                userPaymentResult.postValue(new UserPaymentResult(e));
            }
        });
    }

    public void getUserPayment() {
        CreditCardService.getUserPayment(account.getValue(), password.getValue(), new UserPaymentCallback() {
            @Override
            public void onSuccess(CreditCard creditCard, MemberCard memberCard) {
                userPaymentResult.postValue(new UserPaymentResult(creditCard,memberCard));
            }

            @Override
            public void onFailure(Exception e) {
                userPaymentResult.postValue(new UserPaymentResult(e));
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
