package com.example.escooter.ui.personinfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPersonInfoEditProfileBinding;
import com.example.escooter.databinding.FragmentPersonInfoBinding;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.viewmodel.UserViewModel;
import com.example.escooter.utils.SimpleTextWatcher;
import com.example.escooter.utils.UriBase64Converter;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;

public class PersonInfoFragment extends Fragment {
    private FragmentPersonInfoBinding binding;
    private UserViewModel userViewModel;
    private ImageView personInfoImageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSIONS);
            } else {
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
            } else {
                pickImageFromGallery();
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                personInfoImageView.setImageURI(imageUri);
                                String base64Image = UriBase64Converter.convertUriToBase64(requireContext(), imageUri);
                                userViewModel.uploadUserPhoto(base64Image);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error processing the selected image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to get the selected image, please try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to get the selected image, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

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
        binding.logoutTextView.setOnClickListener(v -> {
            userViewModel.clearData();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_personinfoFragment_to_loginFragment);
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

        personInfoImageView = dialogBinding.personInfoAddImage;

        setDialogListeners(dialogBinding, dialog);
        setupDialogObservers(dialogBinding);

        return dialog;
    }

    private void setDialogListeners(DialogPersonInfoEditProfileBinding dialogBinding, AlertDialog dialog) {
        dialogBinding.personInfoAddImage.setOnClickListener(b -> {
            requestPermissions();
        });

        SimpleTextWatcher textWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String username = dialogBinding.userName.getText().toString();
                String email = dialogBinding.userEmail.getText().toString();
                String phoneNumber = dialogBinding.userPhoneNumber.getText().toString();
                userViewModel.updataDataChanged(username, email, phoneNumber);
            }
        };

        dialogBinding.userName.addTextChangedListener(textWatcher);
        dialogBinding.userEmail.addTextChangedListener(textWatcher);
        dialogBinding.userPhoneNumber.addTextChangedListener(textWatcher);

        dialogBinding.cancelButton.setOnClickListener(b -> dialog.dismiss());
        dialogBinding.bindButton.setOnClickListener(b -> {
            userViewModel.updateUserData();
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
        binding.personinfobutton.imageView.setImageURI(UriBase64Converter.convertBase64ToUri(requireContext(), user.getPhoto()));
        binding.userName.setText(user.getUserName());
        binding.userPhone.setText(user.getPhoneNumber());
        binding.userEmail.setText(user.getEmail());
        binding.userIdentity.setText("Student");
    }
}