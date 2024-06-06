package com.example.escooter.service;

import com.example.escooter.callback.BooleanCallback;
import com.example.escooter.callback.UserPaymentCallback;
import com.example.escooter.data.model.CreditCard;
import com.example.escooter.data.model.MemberCard;
import com.example.escooter.repository.CreditCardRepository;

public class CreditCardService {
    private final CreditCardRepository CreditCardRepository = new CreditCardRepository();


    public void bindCreditCard(String account, String password, String CardNumber, String VaildThru, String username, String Cvv, BooleanCallback callback) {
        CreditCardRepository.bindCreditCard(account, password, CardNumber, VaildThru, username, Cvv, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isBindCreditCard) {
                callback.onSuccess(isBindCreditCard);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
    public void unbindCreditCard(String account, String password, BooleanCallback callback) {
        CreditCardRepository.unbindCreditCard(account, password, new BooleanCallback() {
            @Override
            public void onSuccess(boolean isUnbindCreditCard) {
                callback.onSuccess(isUnbindCreditCard);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getUserPayment(String account, String password, UserPaymentCallback callback) {
        CreditCardRepository.getUserPayment(account, password, new UserPaymentCallback() {
            @Override
            public void onSuccess(CreditCard creditCard, MemberCard memberCard) {
                callback.onSuccess(creditCard,memberCard);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
