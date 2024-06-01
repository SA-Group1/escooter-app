package com.example.escooter.ui.login;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.example.escooter.databinding.FragmentLoginBinding;
import com.example.escooter.R;
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.ui.user.UserViewModelFactory;
import com.example.escooter.utils.SimpleTextWatcher;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private UserViewModel userViewModel;
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupUI();
        setupObservers();
        setupListeners();
    }

    private void setupUI() {
        binding.account.setText("acc001");
        binding.password.setText("pwd001");

        // Put testing data into view model.
        String account = binding.account.getText().toString();
        String password = binding.password.getText().toString();
        loginViewModel.loginDataChanged(account,password);
    }

    private void setupObservers() {
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), this::updateLoginFormState);
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), this::handleLoginResult);
    }

    private void setupListeners() {
        binding.account.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String account = binding.account.getText().toString();
                String password = binding.password.getText().toString();

                loginViewModel.loginDataChanged(account, password);
            }
        });

        binding.password.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String account = binding.account.getText().toString();
                String password = binding.password.getText().toString();

                loginViewModel.loginDataChanged(account, password);
            }
        });

        binding.password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String account = binding.account.getText().toString();
                String password = binding.password.getText().toString();

                loginViewModel.login(account, password);
            }
            return false;
        });

        binding.signUpButton.setOnClickListener(this::onSignupButtonClicked);
        binding.loginButton.setOnClickListener(this::onLoginButtonClicked);
    }

    private void onSignupButtonClicked(View view) {
        navigateToSignupFragment();
    }

    private void onLoginButtonClicked(View view) {
        String account = binding.account.getText().toString();
        String password = binding.password.getText().toString();

        loginViewModel.login(account,password);
    }

    private void updateLoginFormState(LoginFormState loginFormState) {
        if (loginFormState == null) {
            return;
        }

        setupLoginButtonEnabled(loginFormState.isDataValid());

        if (loginFormState.getUsernameError() != null) {
            binding.account.setError(getString(loginFormState.getUsernameError()));
        }
        if (loginFormState.getPasswordError() != null) {
            binding.textInputLayoutPassword.setError(getString(loginFormState.getPasswordError()));
        }
    }

    private void setupLoginButtonEnabled(boolean isEnabled){
        Button loginButton = binding.loginButton;
        loginButton.setEnabled(isEnabled);
    }

    private void handleLoginResult(LoginResult loginResult) {
        if (loginResult == null) {
            return;
        }

        if (loginResult.getError() != null) {
            showLoginFailed(loginResult.getError());
        }

        if (loginResult.getSuccess() != null) {
            updateUiWithUser(loginResult.getSuccess());
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String account = binding.account.getText().toString();
        String password = binding.password.getText().toString();
        userViewModel.setUserCredential(account,password);

        String welcome = getString(R.string.welcome) + model.getUserName();
        showToast(welcome);
        navigateToMenuFragment();
    }

    private void navigateToMenuFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_loginFragment_to_navigation_menuFragment);
    }

    private void navigateToSignupFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.action_loginFragment_to_signUpFragment);
    }

    private void showLoginFailed(Exception errorString) {
        showToast(errorString.toString());
    }

    private void showToast(String message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


