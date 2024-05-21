package com.example.escooter.ui.rentrecord;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.escooter.R;
import com.example.escooter.adapter.RentRecordListAdapter;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.databinding.FragmentRentRecordBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RentRecordFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentRentRecordBinding binding;
        binding = FragmentRentRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rentRecordList.setLayoutManager(new LinearLayoutManager(getContext()));
        RentRecordListAdapter adapter = getRentRecordListAdapter();
        binding.rentRecordList.setAdapter(adapter);

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

        return root;
    }

    @NonNull
    private RentRecordListAdapter getRentRecordListAdapter() {
        ArrayList<RentalRecord> rentRecordList = new ArrayList<>();

        // 初始化UserViewModel
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 观察UserViewModel中的用户数据
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // 抓取UserData
                JSONObject postData = new JSONObject();
                try {
                    postData.put("account", user.getUserName());
                    postData.put("password", user.getPassword());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                String apiUrl = "http://36.232.88.50:8080/api/getRentalRecordList";
                HttpRequest getRentableEscooterList= new HttpRequest(apiUrl);
                // 發送 HTTP POST 請求
                getRentableEscooterList.httpPost(postData, result -> {

                    try {
                        JSONObject rentalRecordsData = result.getJSONObject("rentalRecords");
                        RentalRecord record = RentalRecord.fromJson(rentalRecordsData);
                        rentRecordList.add(record);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            }
        });

        return new RentRecordListAdapter(rentRecordList);
    }
}