package com.example.escooter.ui.rentrecord;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.escooter.R;
import com.example.escooter.adapter.RentRecordListAdapter;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.databinding.FragmentRentRecordBinding;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

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
    private static RentRecordListAdapter getRentRecordListAdapter() {
        ArrayList<RentalRecord> rentRecordList = new ArrayList<>();

        RentalRecord rentRecord1 = new RentalRecord();

        rentRecord1.setDuration(10);
        rentRecord1.setRentRecordId("7414");
        rentRecord1.setEscooterRentTime("2024/12/8 13:20");
        rentRecord1.setEscooterReturnTime("2024/12/8 13:71");
        rentRecord1.setEscooterModel("NMSL7414");
        rentRecord1.setFeePerMin(50);
        rentRecord1.setEscooterId("hello");
        rentRecord1.setTotalFee(174);

        rentRecordList.add(rentRecord1);

        RentalRecord rentRecord2 = new RentalRecord();
        rentRecord2.setDuration(20);
        rentRecord2.setRentRecordId("7410154");
        rentRecord2.setEscooterRentTime("2024/12/8 13:20");
        rentRecord2.setEscooterReturnTime("2024/12/8 13:71");
        rentRecord2.setEscooterModel("您好");
        rentRecord2.setFeePerMin(50);
        rentRecord2.setEscooterId("hello");
        rentRecord2.setTotalFee(174);

        rentRecordList.add(rentRecord2);
        rentRecordList.add(rentRecord1);
        rentRecordList.add(rentRecord1);
        rentRecordList.add(rentRecord1);
        rentRecordList.add(rentRecord1);
        rentRecordList.add(rentRecord1);

        return new RentRecordListAdapter(rentRecordList);
    }
}