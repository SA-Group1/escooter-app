package com.example.escooter.ui.payment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.escooter.R;
import com.example.escooter.databinding.DialogPaymentAddCreditCardBinding;
import com.example.escooter.databinding.DialogPaymentUnbindCreditCardBinding;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.service.getUserDataService;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class PaymentFragment extends Fragment {
    private String account;
    private String password;
    private String username;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPaymentBinding binding;
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button profile_button = binding.profileButton;
        final Button rent_record_button = binding.rentRecordButton;

        binding.creditcardnfo.setVisibility(View.GONE);

        // 初始化UserViewModel
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 观察UserViewModel中的用户数据
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                username = user.getUserName();
                account = user.getAccount();
                password = user.getPassword();
                // 更新TextView的文本为用户信息
                TextView personNameTextView = binding.personinfobutton.personNameTextView;
                personNameTextView.setText(user.getUserName());
                TextView creditCardTextView = root.findViewById(R.id.creditcard_id);
                String creditCardNumber = user.getCreditCard().getCreditCardNumber();
                System.out.println(creditCardNumber + "31303132131213");
                if (creditCardNumber.isEmpty()){
                    binding.creditcardnfo.setVisibility(View.GONE);
                }else {
                    binding.creditcardnfo.setVisibility(View.VISIBLE);
                    creditCardTextView.setText(creditCardNumber.substring(creditCardNumber.length() - 4));
                }
            }
        });

        binding.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_unbind_credit_card, null, false);
            dialogBuilder.setView(dialogView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            DialogPaymentUnbindCreditCardBinding dialogBinding = DialogPaymentUnbindCreditCardBinding.bind(dialogView);

            dialogBinding.cancelButton.setOnClickListener(b -> {
                dialog.dismiss();
            });

            dialogBinding.unbindButton.setOnClickListener(b -> {
                String apiUrl = "http://36.232.88.50:8080/api/unbindCreditCard";
                JSONObject PostData = new JSONObject();
                try {
                    PostData.put("account", account);
                    PostData.put("password", password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                HttpRequest putupdateUserData= new HttpRequest(apiUrl);
                // 發送 HTTP POST 請求
                putupdateUserData.httpPost(PostData, result -> {
                    try {
                        if (result.getBoolean("status")){
                            System.out.println(result.getString("message"));
                            //信用卡消失
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
                binding.creditcardnfo.setVisibility(View.GONE);
                dialog.dismiss();
            });
        });

        binding.addPaymentButton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_add_credit_card, null, false);
            dialogBuilder.setView(dialogView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            DialogPaymentAddCreditCardBinding dialogBinding = DialogPaymentAddCreditCardBinding.bind(dialogView);

            dialogBinding.cancelButton.setOnClickListener(b -> {
                dialog.dismiss();
            });

            dialogBinding.confirmButton.setOnClickListener(b -> {
                String apiUrl = "http://36.232.88.50:8080/api/bindCreditCard";
                JSONObject PostData = new JSONObject();
                try {
                    JSONObject userJson = new JSONObject();
                    userJson.put("account", account);
                    userJson.put("password", password);

                    JSONObject creditCardJson = new JSONObject();
                    creditCardJson.put("cardNumber", dialogBinding.userCardNumber.getText().toString());
                    creditCardJson.put("expirationDate", dialogBinding.userVaildThru.getText().toString());
                    creditCardJson.put("cardHolderName", username);
                    creditCardJson.put("cvv", dialogBinding.userCvv.getText().toString());

                    PostData.put("user", userJson);
                    PostData.put("creditCard", creditCardJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                HttpRequest putupdateUserData = new HttpRequest(apiUrl);
                // 發送 HTTP POST 請求
                putupdateUserData.httpPost(PostData, result -> {
                    try {
                        if (result.getBoolean("status")) {
                            System.out.println(result.getString("message"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
                dialog.dismiss();
            });
        });

        goback_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_navigation_menuFragment);
        });

        profile_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_personinfoFragment);
        });

        rent_record_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_rentRecordFragment);
        });

        return root;
    }
}