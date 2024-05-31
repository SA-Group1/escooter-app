package com.example.escooter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.escooter.databinding.ActivityMainBinding;
import com.example.escooter.ui.menu.RentViewModel;
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.ui.user.UserViewModelFactory;

public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private RentViewModel rentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory()).get(UserViewModel.class);
        rentViewModel = new ViewModelProvider(this).get(RentViewModel.class);

        // 檢查定位權限
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果沒有權限，請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
