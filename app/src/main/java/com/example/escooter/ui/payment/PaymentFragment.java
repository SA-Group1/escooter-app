package com.example.escooter.ui.payment;

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
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;


public class PaymentFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPaymentBinding binding;
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button profile_button = binding.profileButton;
        final Button rent_record_button = binding.rentRecordButton;

        // 初始化UserViewModel
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // 观察UserViewModel中的用户数据
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // 更新TextView的文本为用户信息
                TextView personNameTextView = binding.personinfobutton.personNameTextView;
                personNameTextView.setText(user.getUserName());
                TextView creditCardTextView = root.findViewById(R.id.creditcard_id);
                String creditCardNumber = user.getCreditCard().getCreditCardNumber();
                creditCardTextView.setText(creditCardNumber.substring(creditCardNumber.length() - 4));
            }
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