package com.example.escooter.ui.personinfo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.service.getUserDataService;
import com.example.escooter.service.putUpdataUserDataService;
import com.example.escooter.ui.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class PersonInfoFragment extends Fragment {

    private String account;
    private String password;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPersonInfoBinding binding = FragmentPersonInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setListeners(binding);
        setUserViewModel(binding);

        return root;
    }

    private void setListeners(FragmentPersonInfoBinding binding) {
        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button payment_button = binding.paymentButton;
        final Button rent_record_button = binding.rentRecordButton;

        binding.editprofilebutton.setOnClickListener(v -> showEditProfileDialog());

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
    }

    private void showEditProfileDialog() {
        AlertDialog dialog = createEditProfileDialog();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private AlertDialog createEditProfileDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_person_info_edit_profile, null, false);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        DialogPersonInfoEditProfileBinding dialogBinding = DialogPersonInfoEditProfileBinding.bind(dialogView);
        dialogBinding.cancelButton.setOnClickListener(b -> dialog.dismiss());
        dialogBinding.bindButton.setOnClickListener(b -> {
            new putUpdataUserDataService(account, password, dialog, dialogBinding);
            new getUserDataService(requireContext(), account, password);
            dialog.dismiss();
        });

        return dialog;
    }

    private void setUserViewModel(FragmentPersonInfoBinding binding) {
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                account = user.getAccount();
                password = user.getPassword();
                updateTextViewInfo(binding, user);
            }
        });
    }

    private void updateTextViewInfo(FragmentPersonInfoBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
        TextView NameTextView = requireActivity().findViewById(R.id.user_name);
        NameTextView.setText(user.getUserName());
        TextView phoneTextView = requireActivity().findViewById(R.id.user_phone);
        phoneTextView.setText(user.getPhoneNumber());
        TextView emailTextView = requireActivity().findViewById(R.id.user_email);
        emailTextView.setText(user.getEmail());
        TextView identityTextView = requireActivity().findViewById(R.id.user_identity);
        identityTextView.setText("Student");
    }
}

