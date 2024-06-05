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
import com.example.escooter.databinding.FragmentRentRecordBinding;

import com.example.escooter.ui.user.UserResult;
import com.example.escooter.viewmodel.UserViewModel;
import com.example.escooter.utils.UriBase64Converter;
import com.example.escooter.viewmodel.RentRecordViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class RentRecordFragment extends Fragment {

    private FragmentRentRecordBinding binding;
    private UserViewModel userViewModel;
    private RentRecordViewModel rentRecordViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        rentRecordViewModel = new ViewModelProvider(requireActivity()).get(RentRecordViewModel.class);

        initializeViews(binding);
        setListeners(binding);
        setupObservers();
        userViewModel.getUserData();
    }

    private void initializeViews(FragmentRentRecordBinding binding) {
        binding.rentRecordList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void setRentRecordListAdapter(FragmentRentRecordBinding binding, ArrayList<RentalRecord> rentRecordList) {
        requireActivity().runOnUiThread(() -> {
            RentRecordListAdapter adapter = new RentRecordListAdapter(rentRecordList);
            binding.rentRecordList.setAdapter(adapter);
        });
    }

    private void setupObservers() {
        userViewModel.getUserResult().observe(getViewLifecycleOwner(), this::handleUserResult);
        rentRecordViewModel.getRentRecordResult().observe(getViewLifecycleOwner(), this::handleRentRecordResult);
    }

    private void handleRentRecordResult(RentRecordResult rentRecordResult) {
        if (rentRecordResult == null) {
            return;
        }
        if (rentRecordResult.getError() != null) {
            showFailed(rentRecordResult.getError());
        }
        if (rentRecordResult.getRentalRecord() != null) {
            ArrayList<RentalRecord> rentRecordList = rentRecordResult.getRentalRecord();
            setRentRecordListAdapter(binding, rentRecordList);
        }
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
            rentRecordViewModel.setUserCredential(user.getAccount(),user.getPassword());
            rentRecordViewModel.getRentalRecordList();
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

    private void updateTextViewInfo(FragmentRentRecordBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
        binding.personinfobutton.imageView.setImageURI(UriBase64Converter.convertBase64ToUri(requireContext(), user.getPhoto()));
    }
}