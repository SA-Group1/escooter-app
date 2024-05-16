package com.example.escooter.ui.signup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.escooter.R;
import com.example.escooter.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private String[] spinnerOptions;  // 存储资源数据，避免重复访问

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用View Binding初始化布局
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinnerOptions = getResources().getStringArray(R.array.spinner_options);  // 加载一次，复用

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
            binding.spinner.setAdapter(adapter);

            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) { // 第一项是提示，不执行任何操作
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        Toast.makeText(context, "选择了: " + selectedItem, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 可以留空
                }
            });
        }
    }

    private class CustomSpinnerAdapter extends ArrayAdapter<String> {
        CustomSpinnerAdapter(Context context, int resource, int dropdownResource, String[] objects) {
            super(context, resource, objects);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 清除绑定引用，避免内存泄漏
    }
}
