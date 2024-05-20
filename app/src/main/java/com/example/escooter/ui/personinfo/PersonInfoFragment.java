package com.example.escooter.ui.personinfo;

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
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

public class PersonInfoFragment extends Fragment {

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

