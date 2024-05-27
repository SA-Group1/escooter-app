package com.example.escooter.ui.rentrecord;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.escooter.R;
import com.example.escooter.adapter.RentRecordListAdapter;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.databinding.FragmentRentRecordBinding;
import com.example.escooter.network.HttpRequest;

import com.example.escooter.ui.user.UserResult;
import com.example.escooter.ui.user.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RentRecordFragment extends Fragment {

    private FragmentRentRecordBinding binding;
    private UserViewModel userViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        initializeViews(binding);
        setListeners(binding);
        setupObservers();
        userViewModel.getUserData();
    }

    private void initializeViews(FragmentRentRecordBinding binding) {
        binding.rentRecordList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

//    private void getRentRecordListAdapter(FragmentRentRecordBinding binding) {
//        ArrayList<RentalRecord> rentRecordList = new ArrayList<>();
//
//        // 抓取UserData
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("account", account);
//            postData.put("password", password);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        String apiUrl = "http://36.232.110.240:8080/api/getRentalRecordList";
//        HttpRequest getRentalRecordList= new HttpRequest(apiUrl);
//        // 發送 HTTP POST 請求
//        getRentalRecordList.httpPost(postData, result -> {
//            try {
//                JSONArray rentalRecordsArray = result.getJSONArray("rentalRecords");
//                System.out.println(result.getJSONArray("rentalRecords"));
//
//                for (int i = 0; i < rentalRecordsArray.length(); i++) {
//                    JSONObject recordObject = rentalRecordsArray.getJSONObject(i);
//                    RentalRecord record = RentalRecord.fromJson(recordObject);
//                    rentRecordList.add(record);
//                }
//
//                setRentRecordListAdapter(binding, rentRecordList);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }
    private void setRentRecordListAdapter(FragmentRentRecordBinding binding, ArrayList<RentalRecord> rentRecordList) {
        requireActivity().runOnUiThread(() -> {
            RentRecordListAdapter adapter = new RentRecordListAdapter(rentRecordList);
            binding.rentRecordList.setAdapter(adapter);
        });
    }

    private void setupObservers() {
        userViewModel.getUserResult().observe(getViewLifecycleOwner(), this::handleUserResult);
    }
    private void handleUserResult(UserResult userResult) {

        if (userResult == null) {
            return;
        }
        if (userResult.getError() != null) {
            showFailed(userResult.getError());
        }
        if (userResult.getUser() != null) {
            User user = userResult.getUser();
            updateTextViewInfo(binding, user);
        }
    }
    private void showFailed(Exception errorString) {
        showToast(errorString.toString());
    }
    private void showToast(String message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void setListeners(FragmentRentRecordBinding binding) {
        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button payment_button = binding.paymentButton;
        final Button profile_button = binding.profileButton;


        goback_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_rentRecordFragment_to_navigation_menuFragment);
        });

        payment_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_rentRecordFragment_to_paymentFragment);
        });

        profile_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_rentRecordFragment_to_personinfoFragment);
        });
    }

//    private void setUserViewModel(FragmentRentRecordBinding binding) {
//        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
//        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
//            if (user != null) {
//                account = user.getAccount();
//                password = user.getPassword();
//                updateTextViewInfo(binding, user);
//                getRentRecordListAdapter(binding);
//            }
//        });
//    }

    private void updateTextViewInfo(FragmentRentRecordBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
    }
}