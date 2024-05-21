package com.example.escooter.ui.menu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.DialogMenuRentInfoBinding;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.viewmodel.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private UserViewModel userViewModel;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Polyline currentPolyline;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ConstraintLayout personinfobutton = binding.personinfobutton.getRoot();
        personinfobutton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_personinfoFragment);
        });

        // 獲取 SupportMapFragment 的實例
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // 異步加載地圖
        assert mapFragment != null;
        mapFragment.getMapAsync(callback);

        // 初始化 FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 建立位置回調
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null){
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
                    }
                }
            }
        };
        startLocationUpdates();

        // 观察UserViewModel中的用户数据
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // 更新personNameTextView的文本为用户名
                TextView personNameTextView = binding.personinfobutton.personNameTextView;;
                personNameTextView.setText(user.getUserName());
            }
        });

        return root;
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(1000)
                .setMinUpdateIntervalMillis(500)
                .build();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            googleMap = map;
            // 開啟我的位置功能
            updateLocation();
            // 設定多邊形
//            setPolygon();
            // 設定點選事件
            setOnMarkerClick();
            // 新增車輛位置
            setRentableEscooter();
        }
    };

    private void updateLocation() {
        //設定地圖上的位置控制項
        if (googleMap == null) {
            return;
        }
        try {
            //設定google map的顯示格式與樣式
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //設定google map的我的位置與定位按鈕
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                //設定固定位置
                LatLng startLocation = new LatLng(23.693802, 120.533492);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 18));
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Exception: %s" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setPolygon() {
        // 新增多邊形
        Polygon polygon = googleMap.addPolygon(new PolygonOptions().clickable(true).add(
                new LatLng(23.6948, 120.5313),
                new LatLng(23.6960, 120.5379),
                new LatLng(23.6893, 120.5383),
                new LatLng(23.6882, 120.5332)));
        polygon.setTag("alpha");
        polygon.setStrokeColor(0x5000ABA5);
        polygon.setStrokeWidth(8);
        polygon.setFillColor(0x2000ABA5);
    }

    private void setOnMarkerClick() {
        //設定標籤點擊事件
        googleMap.setOnMarkerClickListener(marker -> {
            //點擊標籤出現引導線
            if (currentPolyline != null) {
                currentPolyline.remove();
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng markerLatLng = marker.getPosition();
                    PolylineOptions Polyline = new PolylineOptions()
                            .add(currentLatLng)
                            .add(markerLatLng)
                            .color(0xffD08343)
                            .width(14);
                    currentPolyline = googleMap.addPolyline(Polyline);
                }
            });

            dialogSet(requireContext());
            return true;
        });
    }
    private void setRentableEscooter(){
        String apiUrl = "http://36.232.88.50:8080/api/getRentableEscooterList";
        JSONObject postData = new JSONObject();
        try {
            postData.put("longitude", "120.534454");
            postData.put("latitude", "23.689305");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        HttpRequest getRentableEscooterList= new HttpRequest(apiUrl);
        // 發送 HTTP POST 請求
        getRentableEscooterList.httpPost(postData, result -> {
            try {
                //json檔案資料處理
                JSONObject escooters = result.getJSONObject("escooters");
                Iterator<String> keys = escooters.keys();
                while (keys.hasNext()){
                    String key = keys.next();
                    JSONObject escooter = escooters.getJSONObject(key);
                    String escooterId = escooter.getString("escooterId");
                    JSONObject gps = escooter.getJSONObject("gps");
                    double latitude = gps.getDouble("latitude");
                    double longitude = gps.getDouble("longitude");

                    //切換為ui線程，才能使用google map的更改
                    runOnUiThread(() -> {
                        LatLng Escooter = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(Escooter).title("E-scooter"+escooterId).icon(BitmapDescriptorFactory.fromResource(R.drawable.escooter)));
                    });
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void runOnUiThread(Runnable action) {
        //切換為ui線程
        new Handler(Looper.getMainLooper()).post(action);
    }
    private void dialogSet(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_menu_rent_info, null, false);
        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        DialogMenuRentInfoBinding dialogBinding = DialogMenuRentInfoBinding.bind(dialogView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 在 Fragment 銷毀時停止fusedLocationProviderClient
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}