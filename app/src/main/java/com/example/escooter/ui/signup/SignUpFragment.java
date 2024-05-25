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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.LoggedInUser;
import com.example.escooter.databinding.FragmentSignUpBinding;
import com.example.escooter.network.HttpRequest;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private String[] spinnerOptions;  // 存储资源数据，避免重复访问
    private static final int PHONE_NUMBER_MAX_LENGTH = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用View Binding初始化布局
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ShapeableImageView goback_button = binding.gobackbutton;
        spinnerOptions = getResources().getStringArray(R.array.spinner_options);  // 加载一次，复用
        final AppCompatButton signUpButton = binding.signUpButton;

        // 初始化按钮状态
        signUpButton.setEnabled(false);

        // 添加TextWatcher来监控输入字段的变化
        addTextWatchers();

        // 设置电话号码输入限制
        setPhoneNumberInputFilter();

        signUpButton.setOnClickListener(v -> {
            if (validateInputs()) {
                JSONObject postData = new JSONObject();
                try {
                    // 準備 POST 請求的資料
                    postData.put("account", binding.userAccount.getText().toString());
                    postData.put("password", binding.userPassword.getText().toString());
                    postData.put("userName", binding.userName.getText().toString());
                    postData.put("email", binding.userEmail.getText().toString());
                    postData.put("phoneNumber", binding.userPhoneNumber.getText().toString());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String registerUrl = "http://36.232.88.50:8080/api/register";
                HttpRequest registerData = new HttpRequest(registerUrl);

                registerData.httpPost(postData, Result -> {
                    try {
                        if (Result.getBoolean("status")) {
                            String message = Result.getString("message");
                            getActivity().runOnUiThread(() -> {Toast.makeText(getContext(),message , Toast.LENGTH_SHORT).show();});
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
                        } else {
                            getActivity().runOnUiThread(() -> {Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();});
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
//                int selectedPosition = binding.userIdentity.getSelectedItemPosition();

            } else {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        goback_button.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_signUpFragment_to_loginFragment);
        });


        // 设置Spinner和事件监听器
        setUpSpinner();

        return root;
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
                    validateInputs();  // 当选择身份时也验证输入
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 可以留空
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
                textView.setTextColor(Color.GRAY);
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
