package com.example.escooter.ui.personinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
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

