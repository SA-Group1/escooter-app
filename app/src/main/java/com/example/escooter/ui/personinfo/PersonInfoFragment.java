package com.example.escooter.ui.personinfo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.ui.user.UserFormState;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.utils.SimpleTextWatcher;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class PersonInfoFragment extends Fragment {
    private FragmentPersonInfoBinding binding;
    private UserViewModel userViewModel;
    private String account;
    private String password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setListeners(binding);
        setupObservers();
        userViewModel.getUserData();
    }

    private void setupObservers() {
        userViewModel.getUserResult().observe(getViewLifecycleOwner(), this::handleUserResult);
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

        setDialogListeners(dialogBinding,dialog);
        setupDialogObservers(dialogBinding);

        return dialog;
    }



    private void setDialogListeners(DialogPersonInfoEditProfileBinding dialogBinding, AlertDialog dialog) {

        dialogBinding.userName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = dialogBinding.userName.getText().toString();
                String email = dialogBinding.userEmail.getText().toString();
                String phoneNumber = dialogBinding.userPhoneNumber.getText().toString();

                userViewModel.updataDataChanged(username, email, phoneNumber);
            }
        });
        dialogBinding.userEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = dialogBinding.userName.getText().toString();
                String email = dialogBinding.userEmail.getText().toString();
                String phoneNumber = dialogBinding.userPhoneNumber.getText().toString();

                userViewModel.updataDataChanged(username, email, phoneNumber);
            }
        });
        dialogBinding.userPhoneNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = dialogBinding.userName.getText().toString();
                String email = dialogBinding.userEmail.getText().toString();
                String phoneNumber = dialogBinding.userPhoneNumber.getText().toString();

                userViewModel.updataDataChanged(username, email, phoneNumber);
            }
        });

        dialogBinding.cancelButton.setOnClickListener(b -> dialog.dismiss());
        dialogBinding.bindButton.setOnClickListener(b -> {
            userViewModel.updataUserData();
            dialog.dismiss();
        });
    }
    private void setupDialogObservers(DialogPersonInfoEditProfileBinding dialogBinding) {
        userViewModel.getUserFormState().observe(getViewLifecycleOwner(), userFormState -> {
            if (userFormState == null) {
                return;
            }

            dialogBinding.bindButton.setEnabled(userFormState.isDataValid());

            if (userFormState.getUsernameError() != null) {
                dialogBinding.userName.setError(getString(userFormState.getUsernameError()));
            }
            if (userFormState.getEmailError() != null) {
                dialogBinding.userEmail.setError(getString(userFormState.getEmailError()));
            }
            if (userFormState.getPhoneNumberError() != null) {
                dialogBinding.userPhoneNumber.setError(getString(userFormState.getPhoneNumberError()));
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

