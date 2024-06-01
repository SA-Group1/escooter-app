package com.example.escooter.ui.return_successful;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.databinding.FragmentReturnSuccessfulBinding;
import com.example.escooter.ui.menu.RentViewModel;
import com.example.escooter.ui.menu.ReturnResult;
import com.example.escooter.ui.user.UserViewModel;


public class ReturnSuccessfulFragment extends Fragment {
    private FragmentReturnSuccessfulBinding binding;
    private RentViewModel rentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReturnSuccessfulBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rentViewModel = new ViewModelProvider(requireActivity()).get(RentViewModel.class);

        setListeners();
        setupObservers();
//        rentViewModel.getReturnResult();

    }

    private void setupObservers() {
        rentViewModel.getReturnResult().observe(getViewLifecycleOwner(), this::handleReturnResult);
    }

    private void handleReturnResult(ReturnResult returnResult) {
        if (returnResult == null) {
            return;
        }
        if (returnResult.getError() != null) {
            showFailed(returnResult.getError());
        }
        if (returnResult.getRentalRecord() != null) {
            RentalRecord escooter = returnResult.getRentalRecord();
            binding.escooterRentTime.setText(escooter.getStartTime());
            binding.escooterReturnTime.setText(escooter.getEndTime());
            binding.feePerMin.setText(String.valueOf(escooter.getFeePerMinutes()));
            binding.duration.setText(String.valueOf(escooter.getDuration()));
            binding.totalFee.setText(String.valueOf(escooter.getTotalFee()));
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


    private void setListeners() {
        binding.continueButton.setOnClickListener(v -> {
            rentViewModel.clearReturnResult();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_returnSuccessFragment_to_navigation_menu);
        });
    }

    private void updateTextViewInfo(FragmentReturnSuccessfulBinding binding, RentalRecord escooter) {
        binding.escooterRentTime.setText(escooter.getStartTime());
        binding.escooterReturnTime.setText(escooter.getEndTime());
        binding.feePerMin.setText(String.valueOf(escooter.getFeePerMinutes()));
        binding.duration.setText(escooter.getDuration());
        binding.totalFee.setText(String.valueOf(escooter.getTotalFee()));
    }

}
