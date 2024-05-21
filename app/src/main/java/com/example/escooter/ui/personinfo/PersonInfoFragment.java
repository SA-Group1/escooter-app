package com.example.escooter.ui.personinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.service.getUserDataService;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

public class PersonInfoFragment extends Fragment {

    private UserViewModel userViewModel;
    private String account;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPersonInfoBinding binding;
        binding = FragmentPersonInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button payment_button = binding.paymentButton;
        final Button rent_record_button = binding.rentRecordButton;

        // 初始化UserViewModel
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 观察UserViewModel中的用户数据
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                account = user.getAccount();
                password = user.getPassword();
                // 更新TextView的文本为用户信息
                TextView personNameTextView = binding.personinfobutton.personNameTextView;
                personNameTextView.setText(user.getUserName());
                TextView NameTextView = root.findViewById(R.id.user_name);
                NameTextView.setText(user.getUserName());
                TextView phoneTextView = root.findViewById(R.id.user_phone);
                phoneTextView.setText(user.getPhoneNumber());
                TextView emailTextView = root.findViewById(R.id.user_email);
                emailTextView.setText(user.getEmail());
                TextView identityTextView = root.findViewById(R.id.user_identity);
                identityTextView.setText("Student");
            }
        });

        binding.editprofilebutton.setOnClickListener(v -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_person_info_edit_profile, null, false);
            dialogBuilder.setView(dialogView);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            DialogPersonInfoEditProfileBinding dialogBinding = DialogPersonInfoEditProfileBinding.bind(dialogView);

            dialogBinding.cancelButton.setOnClickListener(b -> {
                dialog.dismiss();
            });

            dialogBinding.bindButton.setOnClickListener(b -> {
                String apiUrl = "http://36.232.88.50:8080/api/updateUserData";
                JSONObject putData = new JSONObject();
                try {
                    putData.put("account", account);
                    putData.put("password", password);
                    putData.put("userName", dialogBinding.userName.getText().toString());
                    putData.put("email", dialogBinding.userEmail.getText().toString());
                    putData.put("phoneNumber", dialogBinding.userPhoneNumber.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                HttpRequest putupdateUserData= new HttpRequest(apiUrl);
                // 發送 HTTP POST 請求
                putupdateUserData.httpPut(putData, result -> {
                    try {
                        if (result.getBoolean("status")){
                            System.out.println(result.getString("message"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
                new getUserDataService(requireContext(), account, password);
                dialog.dismiss();
            });
        });

        goback_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_navigation_menu);
        });

        payment_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_paymentFragment);
        });

        rent_record_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_rentRecordFragment);
        });

        return root;
    }
}

