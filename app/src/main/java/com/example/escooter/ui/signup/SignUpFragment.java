package com.example.escooter.ui.signup;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.databinding.FragmentSignUpBinding;
import com.example.escooter.viewmodel.SignUpViewModel;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private SignUpViewModel signUpViewModel;
    private String[] spinnerOptions;
    private static final int PHONE_NUMBER_MAX_LENGTH = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerOptions = getResources().getStringArray(R.array.spinner_options);  // 加载一次，复用
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        binding.signUpButton.setEnabled(false);

        addTextWatchers();

        setPhoneNumberInputFilter();
        setupObservers();
        setupListeners();

        setUpSpinner();
    }

    private void setupObservers() {
        signUpViewModel.getSignUpResult().observe(getViewLifecycleOwner(), this::handleSignUpResult);
    }

    private void handleSignUpResult(SignUpResult signUpResult) {
        if (signUpResult == null) {
            return;
        }
        if (signUpResult.getError() != null) {
            showFailed(signUpResult.getError());
        }
        if (signUpResult.getSignUp()) {

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

    private void setupListeners() {
        binding.signUpButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String account = binding.userAccount.getText().toString().trim();
                String password = binding.userPassword.getText().toString().trim();
                String username = binding.userName.getText().toString().trim();
                String email = binding.userEmail.getText().toString().trim();
                String phoneNumber = binding.userPhoneNumber.getText().toString().trim();
                signUpViewModel.SignUp(account, password, username, email, phoneNumber);
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_signUpFragment_to_loginFragment);
            } else {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        binding.gobackbutton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });
    }

    private void setUpSpinner() {
        Context context = getContext();
        if (context != null) {
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(
                    context,
                    R.layout.spinner_title_layout,
                    R.layout.spinner_dropdown_item,
                    spinnerOptions
            );
            binding.userIdentity.setAdapter(adapter);

            binding.userIdentity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    validateInputs();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void setPhoneNumberInputFilter() {
        InputFilter lengthFilter = new InputFilter.LengthFilter(PHONE_NUMBER_MAX_LENGTH);
        binding.userPhoneNumber.setFilters(new InputFilter[]{lengthFilter});
    }

    private void addTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.userAccount.addTextChangedListener(textWatcher);
        binding.userPassword.addTextChangedListener(textWatcher);
        binding.userName.addTextChangedListener(textWatcher);
        binding.userEmail.addTextChangedListener(textWatcher);
    }

    private boolean validateInputs() {
        String account = binding.userAccount.getText().toString().trim();
        String password = binding.userPassword.getText().toString().trim();
        String username = binding.userName.getText().toString().trim();
        String email = binding.userEmail.getText().toString().trim();
        String phoneNumber = binding.userPhoneNumber.getText().toString().trim();
        int selectedPosition = binding.userIdentity.getSelectedItemPosition();

        boolean isSpinnerValid = selectedPosition != 0;  // 确保选择了非提示项
        boolean isValid =!account.isEmpty() && !password.isEmpty() && !username.isEmpty() && !phoneNumber.isEmpty() && !email.isEmpty() && phoneNumber.length() <= PHONE_NUMBER_MAX_LENGTH && isSpinnerValid;
        binding.signUpButton.setEnabled(isValid);
        return isValid;
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<String> {
        private int titleLayout;
        private int dropdownLayout;
        private String[] items;

        CustomSpinnerAdapter(Context context, int titleLayout, int dropdownLayout, String[] objects) {
            super(context, titleLayout, objects);
            this.titleLayout = titleLayout;
            this.dropdownLayout = dropdownLayout;
            this.items = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(titleLayout, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.text1);
            if (position == 0) {
                // Set the hint text color gray
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_medium_gray));
            } else {
                textView.setTextColor(Color.WHITE);
            }
            textView.setText(items[position]);
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return position != 0;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(dropdownLayout, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.text2);
            if (position == 0) {
                // Set the hint text color gray
                textView.setTextColor(Color.GRAY);
            } else {
                textView.setTextColor(Color.BLACK);
            }
            textView.setText(items[position]);

            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 清除绑定引用，避免内存泄漏
    }
}
