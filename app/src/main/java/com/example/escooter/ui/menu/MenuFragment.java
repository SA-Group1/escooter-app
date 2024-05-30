package com.example.escooter.ui.menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.escooter.R;
import com.example.escooter.data.model.Escooter;
import com.example.escooter.data.model.User;
import com.example.escooter.databinding.ComponentMenuRentInfoBinding;
import com.example.escooter.databinding.ComponentMenuScooterInfoBinding;
import com.example.escooter.databinding.FragmentMenuBinding;
import com.example.escooter.databinding.FragmentPaymentBinding;
import com.example.escooter.network.HttpRequest;
import com.example.escooter.ui.user.UserResult;
import com.example.escooter.ui.user.UserViewModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private List<Escooter> escooterList;
    private View view = null;

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
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        final ConstraintLayout personinfobutton = binding.personinfobutton.getRoot();
        personinfobutton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_personinfoFragment);
        });

        rentViewModel = new ViewModelProvider(this).get(RentViewModel.class);

        setupFusedLocation();
        startLocationUpdates();
        setupMapFragment();
        setupObservers();
        setupListeners();
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
                    String ownLatitude = String.valueOf(location.getLatitude());
                    String ownLongitude = String.valueOf(location.getLongitude());

                    rentViewModel.setUserlocation(ownLongitude,ownLatitude);
                    setRentableEscooter();
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
        rentViewModel.getRentResult().observe(getViewLifecycleOwner(), this::handleRentResult);
        rentViewModel.getParkResult().observe(getViewLifecycleOwner(), this::handleParkResult);
    }

    private void handleParkResult(ParkResult parkResult) {
        if (parkResult == null) {
            return;
        }
        if (parkResult.getError() != null) {
            showFailed(parkResult.getError());
        }
        if (parkResult.getEscooterList()) {
            //改變按鈕顏色寫這邊
        }
    }

    private void handleRentResult(RentResult rentResult) {
        if (rentResult == null) {
            return;
        }
        if (rentResult.getError() != null) {
            showFailed(rentResult.getError());
        }
        if (rentResult.getEscooterList() != null) {
            escooterList = rentResult.getEscooterList();
            for (Escooter escooter : escooterList) {
                double latitude = escooter.getLatitude();
                double longitude = escooter.getLongitude();
                String escooterId = escooter.getEscooterId();

                LatLng escooterLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(escooterLocation)
                        .title(escooterId)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_escooter)));
            }
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
            rentViewModel.setUserCredential(user.getAccount(),user.getPassword());
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
            // 設定多邊形
//            setPolygon();
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
        String markerEscooterId = marker.getTitle();

        ViewStub stub = inflateViewStub(context, R.layout.component_menu_rent_info);
        ComponentMenuRentInfoBinding rentInfoBinding = ComponentMenuRentInfoBinding.bind(view);
        markerEscooterInfo(rentInfoBinding, markerEscooterId);

        rentInfoBinding.rentButton.setOnClickListener(v ->{
            inflateNewViewStub(context, stub, R.layout.component_menu_scooter_info);
            ComponentMenuScooterInfoBinding scooterInfoBinding = ComponentMenuScooterInfoBinding.bind(view);
            rentEscooterInfo(scooterInfoBinding, markerEscooterId);

            scooterInfoBinding.parkButton.setOnClickListener(b ->{
                setParking();
            });
            scooterInfoBinding.returnButton.setOnClickListener(b ->{
                //api傳送(service)
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_menu_to_returnSuccessFragment);
            });
        });
    }

    private void rentEscooterInfo(ComponentMenuScooterInfoBinding binding,String markerEscooterId) {
        rentViewModel.rentEscooter();
        for (Escooter escooter : escooterList) {

            binding.scooterId.setText(markerEscooterId);
            binding.scooterModel.setText(escooter.getModelId());
            binding.batteryTimeText.setText(String.valueOf(escooter.getBatteryLevel()));
            //日期
            binding.feePerMin.setText(String.valueOf(escooter.getFeePerMinutes()));
        }
    }

    private ViewStub inflateViewStub(Context context, int layoutResource) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        ViewStub stub = binding.viewStub;
        stub.setLayoutResource(layoutResource);
        view = stub.inflate();
        return stub;
    }

    private void inflateNewViewStub(Context context,ViewStub stub, int layoutResource) {
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
        newstub.setLayoutResource(layoutResource);

        view = newstub.inflate();
    }


    private void markerEscooterInfo(ComponentMenuRentInfoBinding binding, String markerEscooterId) {
        for (Escooter escooter : escooterList) {
            if (Objects.equals(markerEscooterId, escooter.getEscooterId())){
                rentViewModel.setEscooterId(escooter.getEscooterId());
                binding.scooterId.setText(escooter.getEscooterId());
                binding.scooterModel.setText(escooter.getModelId());
                binding.batteryTimeText.setText(String.valueOf(escooter.getBatteryLevel()));
                binding.distanceText.setText("1231");
                binding.rentFee.setText(String.valueOf(escooter.getFeePerMinutes()));
                binding.maxSpeedText.setText("25");
            }
        }
    }


    private void setParking() {
    }

    private void setListeners(FragmentPaymentBinding binding) {

    }

    private void setRentableEscooter() {
        rentViewModel.getRentableEscooterList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}