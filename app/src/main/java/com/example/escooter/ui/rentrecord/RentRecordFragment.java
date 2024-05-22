package com.example.escooter.ui.rentrecord;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.escooter.R;
import com.example.escooter.adapter.RentRecordListAdapter;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.databinding.FragmentRentRecordBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.service.getUserDataService;
import com.example.escooter.service.putUpdataUserDataService;
import com.example.escooter.ui.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class RentRecordFragment extends Fragment {

    private String account;
    private String password;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentRentRecordBinding binding = FragmentRentRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews(binding);
        setListeners(binding);
        setUserViewModel(binding);

        return root;
    }
    private void initializeViews(FragmentRentRecordBinding binding) {
        binding.rentRecordList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @NonNull
    private RentRecordListAdapter getRentRecordListAdapter(FragmentRentRecordBinding binding) {
        ArrayList<RentalRecord> rentRecordList = new ArrayList<>();

        // 抓取UserData
        JSONObject postData = new JSONObject();
        try {
            postData.put("account", account);
            postData.put("password", password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String apiUrl = "http://36.232.88.50:8080/api/getRentalRecordList";
        HttpRequest getRentalRecordList= new HttpRequest(apiUrl);
        // 發送 HTTP POST 請求
        getRentalRecordList.httpPost(postData, result -> {
            try {
                JSONArray rentalRecordsArray = result.getJSONArray("rentalRecords");
                System.out.println(result.getJSONArray("rentalRecords"));

                for (int i = 0; i < rentalRecordsArray.length(); i++) {
                    JSONObject recordObject = rentalRecordsArray.getJSONObject(i);
                    RentalRecord record = RentalRecord.fromJson(recordObject);
                    rentRecordList.add(record);
                }

                setRentRecordListAdapter(binding, rentRecordList);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }
    private void setRentRecordListAdapter(FragmentRentRecordBinding binding, ArrayList<RentalRecord> rentRecordList) {
        getActivity().runOnUiThread(() -> {
            RentRecordListAdapter adapter = new RentRecordListAdapter(rentRecordList);
            binding.rentRecordList.setAdapter(adapter);
        });
    }

    private void setListeners(FragmentRentRecordBinding binding) {
        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button payment_button = binding.paymentButton;
        final Button rent_record_button = binding.rentRecordButton;

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
    }

    private void setUserViewModel(FragmentRentRecordBinding binding) {
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                account = user.getAccount();
                password = user.getPassword();
                updateUserInfo(binding, user);
                getRentRecordListAdapter(binding);
            }
        });
    }

    private void updateUserInfo(FragmentRentRecordBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
    }
}