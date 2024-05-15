package com.example.escooter.ui.payment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.escooter.R;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.google.android.material.imageview.ShapeableImageView;


public class PaymentFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPaymentBinding binding;
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ShapeableImageView button = binding.gobackbutton;
        button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_navigation_menu);
        });

        return root;
    }
}