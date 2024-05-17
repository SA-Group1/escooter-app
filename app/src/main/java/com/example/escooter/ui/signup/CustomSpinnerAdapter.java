package com.example.escooter.ui.signup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.escooter.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


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
        textView.setText(items[position]);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(dropdownLayout, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.text2);
        textView.setText(items[position]);
        return convertView;
    }
}
