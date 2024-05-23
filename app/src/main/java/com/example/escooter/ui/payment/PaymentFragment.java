package com.example.escooter.ui.payment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogPaymentAddCreditCardBinding;
import com.example.escooter.databinding.DialogPaymentUnbindCreditCardBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.service.getUserDataService;
import com.example.escooter.service.postBindCreditCardService;
import com.example.escooter.service.postUnbindCreditCardService;
import com.example.escooter.ui.viewmodel.UserViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Objects;


public class PaymentFragment extends Fragment {
    private String account;
    private String password;
    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPaymentBinding binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews(binding);
        setListeners(binding);
        setUserViewModel(binding);

        return root;
    }

    private void initializeViews(FragmentPaymentBinding binding) {
        binding.creditcardnfo.setVisibility(View.GONE);
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
            new postUnbindCreditCardService(account, password);
            new getUserDataService(requireContext(), account, password);
            binding.creditcardnfo.setVisibility(View.GONE);
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
        dialogBinding.cancelButton.setOnClickListener(b -> dialog.dismiss());
        dialogBinding.confirmButton.setOnClickListener(b -> {
            new postBindCreditCardService(account, password, username,dialogBinding);
            new getUserDataService(requireContext(), account, password);
            binding.creditcardnfo.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        return dialog;
    }

    private void setUserViewModel(FragmentPaymentBinding binding) {
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                username = user.getUserName();
                account = user.getAccount();
                password = user.getPassword();
                updateTextViewInfo(binding,user);
            }
        });
    }

    private void updateTextViewInfo(FragmentPaymentBinding binding, User user) {
        TextView personNameTextView = binding.personinfobutton.personNameTextView;
        personNameTextView.setText(user.getUserName());
        //判斷使用者是否曾輸入信用卡
        String creditCardNumber = user.getCreditCard().getCreditCardNumber();
        if (!Objects.equals(creditCardNumber, "null")){
            System.out.println(creditCardNumber);
            binding.creditcardnfo.setVisibility(View.VISIBLE);
            TextView creditCardTextView = requireActivity().findViewById(R.id.creditcard_id);
            creditCardTextView.setText(creditCardNumber);
        }else {
            System.out.println(creditCardNumber);
            binding.creditcardnfo.setVisibility(View.GONE);
        }
    }


}