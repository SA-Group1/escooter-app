package com.example.escooter.ui.menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.ComponentMenuRentInfoBinding;
import com.example.escooter.databinding.ComponentMenuScooterInfoBinding;
import com.example.escooter.databinding.FragmentLoginBinding;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.service.getRentableEscooterListService;
import com.example.escooter.ui.login.LoginResult;
import com.example.escooter.ui.login.LoginViewModel;
import com.example.escooter.ui.login.LoginViewModelFactory;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.ui.user.UserViewModelFactory;
import com.example.escooter.utils.SimpleTextWatcher;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Polyline currentPolyline;
    private UserViewModel userViewModel;
    private View view = null;
    private String account;
    private String password;
//    private String ownLongitude;
//    private String ownLatitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupMapFragment();
        setupObservers();
        setupListeners();
        setupFusedLocation();
        startLocationUpdates();
        userViewModel.getUserData();
    }

    private void setupFusedLocation() {
        // 初始化 FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 建立位置回調
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
                    }
                }
            }
        };
    }

    private void setupMapFragment() {
        // 獲取 SupportMapFragment 的實例
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // 異步加載地圖
        assert mapFragment != null;
        mapFragment.getMapAsync(callback);
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
            account = user.getAccount();
            password = user.getPassword();
            TextView personNameTextView = binding.personinfobutton.personNameTextView;
            personNameTextView.setText(user.getUserName());
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
        final ConstraintLayout personinfobutton = binding.personinfobutton.getRoot();
        personinfobutton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_personinfoFragment);
        });
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
            // 設定自身位置
//            setOwnLocation();
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
            //點擊出現彈窗
            dialogSet(requireContext(), marker);
            return true;
        });
    }

    private void dialogSet(Context context, Marker marker) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        ViewStub stub = binding.viewStub;
        stub.setLayoutResource(R.layout.component_menu_rent_info);
        view = stub.inflate();
        ComponentMenuRentInfoBinding rentInfoBinding = ComponentMenuRentInfoBinding.bind(view);


        rentInfoBinding.rentButton.setOnClickListener(v -> {
            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {
                    parent.removeView(view);
                }
            }
            ViewStub newstub = new ViewStub(context);
            binding.getRoot().addView(newstub);

            newstub.setLayoutParams(stub.getLayoutParams());
            newstub.setVisibility(stub.getVisibility());
            newstub.setInflatedId(stub.getInflatedId());
            newstub.setLayoutResource(R.layout.component_menu_scooter_info);

            view = newstub.inflate();
            ComponentMenuScooterInfoBinding scooterInfoBinding = ComponentMenuScooterInfoBinding.bind(view);

            String escooterId = marker.getTitle();
            String apiUrl = "http://36.232.110.240:8080/api/rentEscooter";
            JSONObject postData = new JSONObject();
            try {
                JSONObject userObject = new JSONObject();
                userObject.put("account", account);
                userObject.put("password", password);

                JSONObject escooterObject = new JSONObject();
                escooterObject.put("escooterId", escooterId);

                postData.put("user", userObject);
                postData.put("escooter", escooterObject);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            HttpRequest getRentableEscooterList = new HttpRequest(apiUrl);
            // 發送 HTTP POST 請求
            getRentableEscooterList.httpPost(postData, result -> {
                try {
                    //json檔案資料處理
                    JSONObject escooter = result.getJSONObject("escooter");
                    requireActivity().runOnUiThread(() -> {
                        try {
                            scooterInfoBinding.scooterId.setText(escooter.getString("escooterId"));
                            scooterInfoBinding.scooterModel.setText(escooter.getString("modelId"));
                            scooterInfoBinding.batteryTimeText.setText(String.valueOf(escooter.getDouble("batteryLevel")));
                            //日期
                            scooterInfoBinding.feePerMin.setText(String.valueOf(escooter.getDouble("feePerMinutes")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            //setListeners()
            scooterInfoBinding.parkButton.setOnClickListener(b ->{
                setParking();
            });
            scooterInfoBinding.returnButton.setOnClickListener(b ->{
                //api傳送(service)
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_menu_to_returnSuccessFragment);
            });
        });


        String escooterId = marker.getTitle();
        System.out.println(escooterId);
        String apiUrl = "http://36.232.110.240:8080/api/getRentableEscooterList";
        JSONObject postData = new JSONObject();
        try {
            postData.put("longitude", "120.534454");
            postData.put("latitude", "23.689305");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        HttpRequest getRentableEscooterList = new HttpRequest(apiUrl);
        // 發送 HTTP POST 請求
        getRentableEscooterList.httpPost(postData, result -> {
            try {
                //json檔案資料處理
                JSONArray escooters = result.getJSONArray("escooters");
                for (int i = 0; i < escooters.length(); i++) {
                    JSONObject escooter = escooters.getJSONObject(i);
                    if (Objects.equals(escooterId, escooter.getString("escooterId"))) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                rentInfoBinding.scooterId.setText(escooter.getString("escooterId"));
                                rentInfoBinding.scooterModel.setText(escooter.getString("modelId"));
                                rentInfoBinding.batteryTimeText.setText(String.valueOf(escooter.getDouble("batteryLevel")));
                                rentInfoBinding.distanceText.setText("1231");
                                rentInfoBinding.rentFee.setText(String.valueOf(escooter.getDouble("feePerMinutes")));
                                rentInfoBinding.maxSpeedText.setText("25");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    private void setParking() {
    }

    private void setListeners(FragmentPaymentBinding binding) {

    }

    private void setRentableEscooter() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String ownLongitude = String.valueOf(location.getLongitude());
                String ownLatitude = String.valueOf(location.getLatitude());
                new getRentableEscooterListService(requireContext(), googleMap, ownLongitude, ownLatitude);
            }
        });
    }

//    private void setOwnLocation() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
//            if (location != null) {
//                ownLongitude = String.valueOf(location.getLongitude());
//                ownLatitude = String.valueOf(location.getLatitude());
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 在 Fragment 銷毀時停止fusedLocationProviderClient
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}