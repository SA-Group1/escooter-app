package com.example.escooter.ui.return_successful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.FragmentReturnSuccessfulBinding;
import com.example.escooter.ui.user.UserViewModel;


public class ReturnSuccessfulFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentReturnSuccessfulBinding binding = FragmentReturnSuccessfulBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setListeners(binding);
//        setUserViewModel(binding);

        return root;
    }


    private void setListeners(FragmentReturnSuccessfulBinding binding) {

        binding.continueButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_returnSuccessFragment_to_navigation_menu);
        });
    }

//    private void setUserViewModel(FragmentReturnSuccessfulBinding binding) {
//        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
//        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
//            if (user != null) {
//                updateTextViewInfo(binding,user);
//            }
//        });
//    }

    private void updateTextViewInfo(FragmentReturnSuccessfulBinding binding, User user) {

    }

}
