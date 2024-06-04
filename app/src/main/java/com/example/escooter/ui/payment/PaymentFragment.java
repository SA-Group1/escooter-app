package com.example.escooter.ui.payment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPaymentAddCreditCardBinding;
import com.example.escooter.databinding.DialogPaymentUnbindCreditCardBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.utils.SimpleTextWatcher;
import com.example.escooter.utils.UriBase64Converter;
import com.example.escooter.viewmodel.CreditCardViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;


public class PaymentFragment extends Fragment {
    private FragmentPaymentBinding binding;
    private UserViewModel userViewModel;
    private CreditCardViewModel creditCardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        creditCardViewModel = new ViewModelProvider(this).get(CreditCardViewModel.class);

        initializeViews(binding);
        setListeners(binding);
        setupObservers();

        userViewModel.getUserData();
    }

    private void initializeViews(FragmentPaymentBinding binding) {
        binding.creditcardnfo.setVisibility(View.GONE);
    }

    private void setupObservers() {
        userViewModel.getUserResult().observe(getViewLifecycleOwner(), this::handleUserResult);
        creditCardViewModel.getCreditCardResult().observe(getViewLifecycleOwner(), this::handleCreditCardResult);
    }

    private void handleCreditCardResult(CreditCardResult creditCardResult) {

        if (creditCardResult == null) {
            return;
        }
        if (creditCardResult.getError() != null) {
            showFailed(creditCardResult.getError());
        }
        if (creditCardResult.getCreditCard() != null) {
            User user = userViewModel.getUserResult().getValue().getUser();
            user.setCreditCard(creditCardResult.getCreditCard());
            updateTextViewInfo(binding,user);
        }
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
            String account = user.getAccount();
            String password = user.getPassword();
            String username = user.getUserName();
            creditCardViewModel.setUserCredential(account,password,username);
            updateTextViewInfo(binding,user);
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

    private void setListeners(FragmentPaymentBinding binding) {
        final ShapeableImageView goback_button = binding.gobackbutton;
        final Button profile_button = binding.profileButton;
        final Button rent_record_button = binding.rentRecordButton;

        binding.deleteButton.setOnClickListener(v -> showUnbindCreditCardDialog(binding));

        binding.addPaymentButton.setOnClickListener(v -> showAddCreditCardDialog(binding));

        goback_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_navigation_menuFragment);
        });

        profile_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_personinfoFragment);
        });

        rent_record_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_paymentFragment_to_rentRecordFragment);
        });
    }

    private void showUnbindCreditCardDialog(FragmentPaymentBinding binding) {
        AlertDialog dialog = createUnbindCreditCardDialog(binding);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private AlertDialog createUnbindCreditCardDialog(FragmentPaymentBinding binding) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_unbind_credit_card, null, false);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        DialogPaymentUnbindCreditCardBinding dialogBinding = DialogPaymentUnbindCreditCardBinding.bind(dialogView);
        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.unbindButton.setOnClickListener(v -> {
            creditCardViewModel.unbindCreditCard();
            dialog.dismiss();
        });

        return dialog;
    }

    private void showAddCreditCardDialog(FragmentPaymentBinding binding) {
        AlertDialog dialog = createAddCreditCardDialog(binding);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    private AlertDialog createAddCreditCardDialog(FragmentPaymentBinding binding) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_payment_add_credit_card, null, false);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        DialogPaymentAddCreditCardBinding dialogBinding = DialogPaymentAddCreditCardBinding.bind(dialogView);

        setDialogListeners(dialogBinding,dialog);
        setupDialogObservers(dialogBinding);

        return dialog;
    }

    private void setDialogListeners(DialogPaymentAddCreditCardBinding dialogBinding, AlertDialog dialog) {

        dialogBinding.userCardNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String CardNumber = dialogBinding.userCardNumber.getText().toString();
                String VaildThru = dialogBinding.userVaildThru.getText().toString();
                String Cvv = dialogBinding.userCvv.getText().toString();

                creditCardViewModel.updataDataChanged(CardNumber, VaildThru, Cvv);
            }
        });
        dialogBinding.userVaildThru.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String CardNumber = dialogBinding.userCardNumber.getText().toString();
                String VaildThru = dialogBinding.userVaildThru.getText().toString();
                String Cvv = dialogBinding.userCvv.getText().toString();

                creditCardViewModel.updataDataChanged(CardNumber, VaildThru, Cvv);
            }
        });
        dialogBinding.userCvv.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String CardNumber = dialogBinding.userCardNumber.getText().toString();
                String VaildThru = dialogBinding.userVaildThru.getText().toString();
                String Cvv = dialogBinding.userCvv.getText().toString();

                creditCardViewModel.updataDataChanged(CardNumber, VaildThru, Cvv);
            }
        });

        dialogBinding.cancelButton.setOnClickListener(b -> dialog.dismiss());
        dialogBinding.confirmButton.setOnClickListener(b -> {

            creditCardViewModel.bindCreditCard();
            dialog.dismiss();
        });
    }
    private void setupDialogObservers(DialogPaymentAddCreditCardBinding dialogBinding) {
        creditCardViewModel.getCreditCardFormState().observe(getViewLifecycleOwner(), CreditCardFormState -> {
            if (CreditCardFormState == null) {
                return;
            }

            dialogBinding.confirmButton.setEnabled(CreditCardFormState.isDataValid());

            if (CreditCardFormState.getCardNumberError() != null) {
                dialogBinding.userCardNumber.setError(getString(CreditCardFormState.getCardNumberError()));
            }
            if (CreditCardFormState.getVaildThruErrorr() != null) {
                dialogBinding.userVaildThru.setError(getString(CreditCardFormState.getVaildThruErrorr()));
            }
            if (CreditCardFormState.getCvvError() != null) {
                dialogBinding.userCvv.setError(getString(CreditCardFormState.getCvvError()));
            }
        });
    }

    private void updateTextViewInfo(FragmentPaymentBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
        binding.personinfobutton.imageView.setImageURI(UriBase64Converter.convertBase64ToUri(requireContext(), user.getPhoto()));
        //判斷使用者是否曾輸入信用卡
        String creditCardNumber = user.getCreditCard().getCreditCardNumber();
        if (!Objects.equals(creditCardNumber, "null")){
            if (creditCardNumber.length() > 4) {
                creditCardNumber = creditCardNumber.substring(creditCardNumber.length() - 4);
            }
            binding.creditcardnfo.setVisibility(View.VISIBLE);
            binding.addPaymentButton.setVisibility(View.GONE);
            TextView creditCardTextView = requireActivity().findViewById(R.id.creditcard_id);
            creditCardTextView.setText(creditCardNumber);
        }else {
            binding.creditcardnfo.setVisibility(View.GONE);
            binding.addPaymentButton.setVisibility(View.VISIBLE);
        }
    }
}