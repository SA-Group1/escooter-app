package com.example.escooter.ui.personinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.databinding.FragmentPersoninfoBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class PersoninfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPersoninfoBinding binding;
        binding = FragmentPersoninfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ShapeableImageView button = binding.gobackbutton;
        button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_navigation_menu);
        });

        return root;
    }
}

