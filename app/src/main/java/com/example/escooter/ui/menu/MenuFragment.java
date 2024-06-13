package com.example.escooter.ui.menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.Gps;
import com.example.escooter.data.model.ReturnAreas;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.ComponentMenuRentInfoBinding;
import com.example.escooter.databinding.ComponentMenuScooterInfoBinding;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.service.EscooterService;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.viewmodel.UserViewModel;
import com.example.escooter.utils.UriBase64Converter;
import com.example.escooter.viewmodel.MapViewModel;
import com.example.escooter.viewmodel.RentViewModel;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Polyline currentPolyline;
    private UserViewModel userViewModel;
    private RentViewModel rentViewModel;
    private MapViewModel mapViewModel;
    private View view = null;
    private EscooterService escooterService;
    private ComponentMenuScooterInfoBinding scooterInfoBinding;
    private Marker marker;
    private boolean isPark = false;
    private double ownLatitude;
    private double ownLongitude;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private List<Escooter> currentEscooterList = new ArrayList<>();

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
        rentViewModel = new ViewModelProvider(requireActivity()).get(RentViewModel.class);
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        escooterService = new EscooterService(getContext());

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setupFusedLocation();
        startLocationUpdates();
        setupMapFragment();
        setupObservers();
        setupListeners();
    }

    private void setupFusedLocation() {
        // 初始化 FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 建立位置回調
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                ownLatitude = location.getLatitude();
                ownLongitude = location.getLongitude();

                float[] results = new float[1];
                Location.distanceBetween(lastLatitude, lastLongitude, ownLatitude, ownLongitude, results);
                float distanceInMeters = results[0];

                if (distanceInMeters > 100) {
                    lastLatitude = ownLatitude;
                    lastLongitude = ownLongitude;

                    rentViewModel.setUserlocation(Double.toString(ownLongitude), Double.toString(ownLatitude));
                    setRentableEscooter();
                }
            }
            }
        };
    }

    private void stopLocationUpdates() {
        if (locationCallback != null && fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
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
        rentViewModel.getRentableListResult().observe(getViewLifecycleOwner(), this::handleRentableListResult);
        rentViewModel.getRentResult().observe(getViewLifecycleOwner(), this::handleRentResult);
        rentViewModel.getParkResult().observe(getViewLifecycleOwner(), this::handleParkResult);
        rentViewModel.getReturnResult().observe(getViewLifecycleOwner(), this::handleReturnResult);
        rentViewModel.getEscooterGpsResult().observe(getViewLifecycleOwner(), this::handleEscooterGpsResult);
        mapViewModel.getMapResult().observe(getViewLifecycleOwner(), this::handleMapResult);
        mapViewModel.getPolylinePoints().observe(getViewLifecycleOwner(), this::handlePolylineResult);
    }

    private void handleRentableListResult(RentableListResult rentableListResult) {
        if (googleMap == null || rentableListResult == null) {
            return;
        }

        if (rentableListResult.getError() != null) {
            Toast.makeText(requireContext().getApplicationContext(), "No escooter nearby", Toast.LENGTH_LONG).show();
            showFailed(rentableListResult.getError());
            return;
        }

        List<Escooter> newEscooterList = rentableListResult.getEscooterList();
        if (newEscooterList != null) {
            for (Escooter newEscooter : newEscooterList) {
                boolean exists = false;
                for (Escooter currentEscooter : currentEscooterList) {
                    if (currentEscooter.getEscooterId().equals(newEscooter.getEscooterId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    double latitude = newEscooter.getLatitude();
                    double longitude = newEscooter.getLongitude();
                    String escooterId = newEscooter.getEscooterId();

                    LatLng escooterLocation = new LatLng(latitude, longitude);

                    googleMap.addMarker(new MarkerOptions()
                            .position(escooterLocation)
                            .title(escooterId)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_escooter)));
                }
            }

            // Update the current escooter list
            currentEscooterList.clear();
            currentEscooterList.addAll(newEscooterList);
        }
    }

    private void handlePolylineResult(List<LatLng> points) {
        if (googleMap == null) {
            return;
        }
        if (points != null && !points.isEmpty()) {
            try {
                if (currentPolyline != null) {
                    currentPolyline.remove();
                }
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(0xffD08343)
                        .width(14);
                currentPolyline = googleMap.addPolyline(polylineOptions);
            } catch (Exception e) {
                Log.e("MapFragment", "Failed to add polyline", e);
                Toast.makeText(requireContext(), "Failed to draw route", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (currentPolyline != null) {
                currentPolyline.remove();
                currentPolyline = null;
            }
        }
    }

    private void handleMapResult(MapResult mapResult) {
        if (googleMap == null) {
            return;
        }
        if (mapResult == null) {
            return;
        }
        if (mapResult.getError() != null) {
            Toast.makeText(requireContext().getApplicationContext(), "Return area not obtained", Toast.LENGTH_LONG).show();
            showFailed(mapResult.getError());
        }
        if (mapResult.getReturnAreas() != null) {
            ReturnAreas returnAreas = mapResult.getReturnAreas();
            for (ReturnAreas.ReturnArea returnArea : returnAreas.getData()) {
                List<LatLng> points = new ArrayList<>();
                for (ReturnAreas.AreaPoint areaPoint : returnArea.getAreaPoint()) {
                    points.add(new LatLng(areaPoint.getLatitude(), areaPoint.getLongitude()));
                }
                setPolygon(points);
            }
        }
    }

    private void handleReturnResult(ReturnResult returnResult) {
        if (googleMap == null) {
            return;
        }
        if (returnResult == null) {
            return;
        }
        if (returnResult.getError() != null) {
            Toast.makeText(requireContext().getApplicationContext(), "Not within the return area", Toast.LENGTH_LONG).show();
            showFailed(returnResult.getError());
        }
        if (returnResult.getRentalRecord() != null) {
            escooterService.stopGpsUpdates();
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_returnSuccessFragment);
        }
    }

    private void handleEscooterGpsResult(EscooterGpsResult escooterGpsResult) {
        if (googleMap == null) {
            return;
        }
        if (escooterGpsResult == null) {
            Toast.makeText(requireContext().getApplicationContext(), "E-scooter GPS not obtained", Toast.LENGTH_LONG).show();
            return;
        }
        if (escooterGpsResult.getError() != null) {
            showFailed(escooterGpsResult.getError());
        }
        if (escooterGpsResult.getEscooterGps() == null) {
            return;
        }

        Gps gps = escooterGpsResult.getEscooterGps();
        LatLng markerLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());

        // 設置距離閾值 20 米
        float distanceThreshold = 20.0f;
        // 計算當前位置與 marker 的距離
        float[] results = new float[1];
        Location.distanceBetween(ownLatitude, ownLongitude,
                markerLatLng.latitude, markerLatLng.longitude, results);
        float distance = results[0];

        // 判斷距離是否超過閾值，如果超過則顯示 marker
        if (distance > distanceThreshold) {
            if (marker == null) {
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(markerLatLng)
                        .title(rentViewModel.getEscooterId().getValue())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_escooter)));
            } else {
                marker.setPosition(markerLatLng);
            }
            // 更新位置信息
            updateRentEscooterInfo(scooterInfoBinding);
        } else {
            if (marker != null) {
                marker.remove(); // 移除 marker
                marker = null;
            }
            if (currentPolyline != null) {
                currentPolyline.remove(); // 移除 polyline
                currentPolyline = null;
            }
        }
    }

    private void handleParkResult(ParkResult parkResult) {
        if (googleMap == null) {
            return;
        }
        if (parkResult == null) {
            return;
        }
        if (parkResult.getError() != null) {
            showFailed(parkResult.getError());
        }
    }

    private void handleRentResult(RentResult rentResult) {
        if (googleMap == null) {
            return;
        }
        if (rentResult == null) {
            return;
        }
        if (rentResult.getError() != null) {
//            Toast.makeText(requireContext().getApplicationContext(), "No escooter nearby", Toast.LENGTH_LONG).show();
            showFailed(rentResult.getError());
        }
        if (rentResult.getEscooter() != null) {

        }
    }

    private void handleUserResult(UserResult userResult) {

        if (userResult == null) {
            return;
        }
        if (userResult.getError() != null) {
            Toast.makeText(requireContext().getApplicationContext(), "User Data not obtained", Toast.LENGTH_LONG).show();
            showFailed(userResult.getError());
        }
        if (userResult.getUser() != null) {
            User user = userResult.getUser();
            rentViewModel.setUserCredential(user.getAccount(),user.getPassword());
            TextView personNameTextView = binding.personinfobutton.personNameTextView;
            personNameTextView.setText(user.getUserName());
            binding.personinfobutton.imageView.setImageURI(UriBase64Converter.convertBase64ToUri(requireContext(), user.getPhoto()));
        }
    }

    private void showFailed(Exception errorString) {
        showToast(errorString.toString());
    }
    private void showToast(String message) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            System.out.println(message);
        }
    }

    private void setupListeners() {
        final ConstraintLayout personinfobutton = binding.personinfobutton.getRoot();
        personinfobutton.setOnClickListener(v -> {
            if (escooterService.getIsGet()){
                escooterService.stopGpsUpdates();
            }
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_personinfoFragment);
        });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(10000) // 10秒更新一次
                .setMinUpdateIntervalMillis(5000) // 最小更新間隔為5秒
                .build();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap map) {
            googleMap = map;
            // 開啟我的位置功能
            userViewModel.getUserData();
            updateLocation();
            mapViewModel.getReturnAreas();
            // 設定點選事件
            setOnMarkerClick();
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

    private void setPolygon(List<LatLng> points) {
        // 新增多邊形
        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(points)); // 添加多邊形的所有點
        polygon.setTag("alpha");
        polygon.setStrokeColor(0x500080FF);
        polygon.setStrokeWidth(8);
        polygon.setFillColor(0x200080FF);
    }

    private void setOnMarkerClick() {
        //設定標籤點擊事件
        googleMap.setOnMarkerClickListener(marker -> {

            //檢查引導線是否已經存在
            if (currentPolyline != null) {
                currentPolyline.remove();
            }
            //停止初始locationCallback
            stopLocationUpdates();
            //新增導引locationCallback
            setlocationCallBack(marker);
            //點擊出現彈窗
            dialogSet(requireContext(), marker);
            //開始LocationUpdates
            startLocationUpdates();

            return true;
        });
    }

    private void setlocationCallBack(Marker marker) {
        // 设置位置更新回调
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng markerLatLng = marker.getPosition();
                mapViewModel.setDirections(currentLatLng,markerLatLng);
                if (googleMap != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                }

                ownLatitude = location.getLatitude();
                ownLongitude = location.getLongitude();

                float[] results = new float[1];
                Location.distanceBetween(lastLatitude, lastLongitude, ownLatitude, ownLongitude, results);
                float distanceInMeters = results[0];

                if (distanceInMeters > 100) {
                    lastLatitude = ownLatitude;
                    lastLongitude = ownLongitude;

                    rentViewModel.setUserlocation(Double.toString(ownLongitude), Double.toString(ownLatitude));
                    setRentableEscooter();
                }

            }
            }
        };
    }



    private void dialogSet(Context context, Marker marker) {
        String markerEscooterId = marker.getTitle();

        ViewStub originalStub = binding.viewStub; // 获取初始ViewStub
        inflateViewStub(context, R.layout.component_menu_rent_info,originalStub);
        ComponentMenuRentInfoBinding rentInfoBinding = ComponentMenuRentInfoBinding.bind(view);
        markerEscooterInfo(rentInfoBinding, markerEscooterId);

        rentInfoBinding.rentButton.setOnClickListener(v ->{

            marker.remove();
            googleMap.clear();
            googleMap.setOnMarkerClickListener(null);
            stopLocationUpdates();
            setRentLocationCallBack(marker);
            startLocationUpdates();
            mapViewModel.getReturnAreas();
            escooterService.resetStartTime();
            escooterService.startGpsUpdates(rentViewModel, mapViewModel);

            //ViewStub彈窗新增
            inflateViewStub(context,R.layout.component_menu_scooter_info,originalStub);
            scooterInfoBinding = ComponentMenuScooterInfoBinding.bind(view);
            rentEscooterInfo(scooterInfoBinding, markerEscooterId);

            scooterInfoBinding.parkButton.setOnClickListener(b ->{
                setParking();

                if (isPark) {
                    scooterInfoBinding.parkButton.setText("Park");
                    isPark = !isPark;

                    int textColor = ContextCompat.getColor(context, R.color.secondary_deep_gray);
                    scooterInfoBinding.parkButton.setTextColor(textColor);
                    scooterInfoBinding.parkButton.setBackgroundTintList(null);
                } else {
                    scooterInfoBinding.parkButton.setText("Unpark");
                    isPark = !isPark;

                    int textColor = ContextCompat.getColor(context, R.color.primary_white);
                    scooterInfoBinding.parkButton.setTextColor(textColor);

                    ColorStateList backgroundTint = ContextCompat.getColorStateList(context, R.color.secondary_dark_gray);
                    scooterInfoBinding.parkButton.setBackgroundTintList(backgroundTint);
                }
            });
            scooterInfoBinding.returnButton.setOnClickListener(b ->{
                rentViewModel.returnEscooter();
            });
        });
    }

    private void setRentLocationCallBack(Marker marker) {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng markerLatLng = marker.getPosition();
                    mapViewModel.setDirections(currentLatLng,markerLatLng);
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                    }
                }
            }
        };
    }


    private void rentEscooterInfo(ComponentMenuScooterInfoBinding binding,String markerEscooterId) {
        rentViewModel.rentEscooter();
        for (Escooter escooter : currentEscooterList) {
            binding.scooterId.setText(markerEscooterId);
            binding.scooterModel.setText(escooter.getModelId());
            binding.batteryTimeText.setText(String.valueOf(escooter.getBatteryLevel()));
            binding.feePerMin.setText(String.valueOf(escooter.getFeePerMinutes()));
        }
    }
    private void updateRentEscooterInfo(ComponentMenuScooterInfoBinding binding) {
        binding.escooterRentTime.setText(escooterService.getStartTime());
        binding.duration.setText(String.valueOf(escooterService.getDuration()));
        binding.totalFee.setText(String.valueOf(escooterService.getTotalCost()));
    }

    private void inflateViewStub(Context context, int layoutResource, ViewStub originalStub) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }

        ViewStub newStub = new ViewStub(context);
        ViewGroup.LayoutParams params = originalStub.getLayoutParams();
        if (params instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) params).bottomMargin =
                    ((ViewGroup.MarginLayoutParams) originalStub.getLayoutParams()).bottomMargin;
        }
        newStub.setLayoutParams(params);
        newStub.setLayoutResource(layoutResource);
        newStub.setId(originalStub.getId());
        newStub.setInflatedId(originalStub.getInflatedId());

        binding.getRoot().removeView(originalStub); // 移除初始ViewStub
        binding.getRoot().addView(newStub); // 添加新的ViewStub

        view = newStub.inflate();
    }

    private void markerEscooterInfo(ComponentMenuRentInfoBinding binding, String markerEscooterId) {
        for (Escooter escooter : currentEscooterList) {
            if (Objects.equals(markerEscooterId, escooter.getEscooterId())){
                rentViewModel.setEscooterId(escooter.getEscooterId());
                binding.scooterId.setText(escooter.getEscooterId());
                binding.scooterModel.setText(escooter.getModelId());
                binding.batteryTimeText.setText(String.valueOf(escooter.getBatteryLevel()));
                binding.distanceText.setText("3");
                binding.rentFee.setText("$ " + escooter.getFeePerMinutes());
                binding.maxSpeedText.setText("25");
            }
        }
    }

    private void setParking() {
        rentViewModel.updateEscooterParkStatus();
    }

    private void setRentableEscooter() {
        rentViewModel.getRentableEscooterList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}