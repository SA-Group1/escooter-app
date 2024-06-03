package com.example.escooter.ui.menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import com.example.escooter.ui.user.UserViewModel;
import com.example.escooter.utils.UriBase64Converter;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private List<Escooter> escooterList;
    private Thread navigationThread;
    private View view = null;
    private EscooterService escooterService;
    private ComponentMenuScooterInfoBinding scooterInfoBinding;

    private boolean isPark = false;

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

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

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
                    System.out.println("初始化");
                    String ownLatitude = String.valueOf(location.getLatitude());
                    String ownLongitude = String.valueOf(location.getLongitude());

                    rentViewModel.setUserlocation(ownLongitude,ownLatitude);
                    setRentableEscooter();
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
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
        rentViewModel.getRentResult().observe(getViewLifecycleOwner(), this::handleRentResult);
        rentViewModel.getParkResult().observe(getViewLifecycleOwner(), this::handleParkResult);
        rentViewModel.getReturnResult().observe(getViewLifecycleOwner(), this::handleReturnResult);
        rentViewModel.getEscooterGpsResult().observe(getViewLifecycleOwner(), this::handleEscooterGpsResult);
        mapViewModel.getMapResult().observe(getViewLifecycleOwner(), this::handleMapResult);
    }

    private void handleMapResult(MapResult mapResult) {
        if (mapResult == null) {
            return;
        }
        if (mapResult.getError() != null) {
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

        if (returnResult == null) {
            return;
        }
        if (returnResult.getError() != null) {
            showFailed(returnResult.getError());
        }
        if (returnResult.getRentalRecord() != null) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_menu_to_returnSuccessFragment);
        }
    }

    private void handleEscooterGpsResult(EscooterGpsResult escooterGpsResult) {
        if (escooterGpsResult == null) {
            return;
        }
        if (escooterGpsResult.getError() != null) {
            showFailed(escooterGpsResult.getError());
        }
        if (escooterGpsResult.getEscooterGps() != null) {

            Gps gps = escooterGpsResult.getEscooterGps();
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng markerLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());

                        if (googleMap != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                        }
                        // 設置距離閾值 20 米
                        float distanceThreshold = 20.0f;
                        // 計算當前位置與 marker 的距離
                        float[] results = new float[1];
                        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                                markerLatLng.latitude, markerLatLng.longitude, results);
                        float distance = results[0];

                        // 判斷距離是否超過閾值，如果超過則顯示 marker
                        if (distance > distanceThreshold) {
                            googleMap.addMarker(new MarkerOptions()
                                    .position(markerLatLng)
                                    .title(rentViewModel.getEscooterId().toString())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_escooter)));
                            getDirections(currentLatLng, markerLatLng);
                        } else {
                            if (currentPolyline != null) {
                                currentPolyline.remove();
                            }
                            googleMap.clear();
                        }
                    }
                }
            };
            //開始LocationUpdates
            startLocationUpdates();
            updataRentEscooterInfo(scooterInfoBinding);
        }
    }

    private void handleParkResult(ParkResult parkResult) {
        if (parkResult == null) {
            return;
        }
        if (parkResult.getError() != null) {
            showFailed(parkResult.getError());
        }
        if (parkResult.getEscooterList()) {

        }
    }

    private void handleRentResult(RentResult rentResult) {
        if(googleMap == null){
            return;
        }
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
            binding.personinfobutton.imageView.setImageURI(UriBase64Converter.convertBase64ToUri(requireContext(), user.getPhoto()));
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
            //開始LocationUpdates
            startLocationUpdates();
            //點擊出現彈窗
            dialogSet(requireContext(), marker);
            return true;
        });
    }

    private void setlocationCallBack(Marker marker) {
        // 设置位置更新回调
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                System.out.println("導引線");
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng markerLatLng = marker.getPosition();
                    if (googleMap != null) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                    }
                    getDirections(currentLatLng, markerLatLng);
                    setRentableEscooter();
                }
            }
        };
    }

    //     使用Directions API獲取導航路徑
    private void getDirections(LatLng origin, LatLng destination) {

        String apiKey = getApiKey();
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=bicycling" + // 指定为骑行模式
                "&alternatives=true" + // 請求多條路線
                "&key=" + apiKey;

        // 發送HTTP請求獲取導航路徑
        navigationThread = new Thread(() -> {
            try {
                URL directionsUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) directionsUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                String response = stringBuilder.toString();
                parseDirectionsResponse(response); // 解析導航路徑並顯示在地圖上

                bufferedReader.close();
                streamReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        navigationThread.start();
    }

    private String getApiKey() {
        try {
            ApplicationInfo appInfo = requireContext().getPackageManager().getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = appInfo.metaData;
            return metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 解析Directions API返回的JSON數據並在地圖上顯示導航路徑
    private void parseDirectionsResponse(String response) {
        requireActivity().runOnUiThread(() -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray routes = jsonResponse.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject bestRoute = selectBestRoute(routes);
                    JSONObject overviewPolyline = bestRoute.getJSONObject("overview_polyline");
                    String encodedPolyline = overviewPolyline.getString("points");
                    List<LatLng> points = decodePolyline(encodedPolyline);

                    // 显示导航路径
                    if (currentPolyline != null) {
                        currentPolyline.remove();
                    }
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(points)
                            .color(0xffD08343)
                            .width(14);
                    currentPolyline = googleMap.addPolyline(polylineOptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private JSONObject selectBestRoute(JSONArray routes) throws JSONException {
        // 根据需求选择最佳路线，例如最短时间或距离
        JSONObject bestRoute = routes.getJSONObject(0);
        for (int i = 1; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            JSONArray legs = route.getJSONArray("legs");
            JSONArray bestRouteLegs = bestRoute.getJSONArray("legs");

            if (legs.length() > 0 && bestRouteLegs.length() > 0) {
                JSONObject leg = legs.getJSONObject(0);
                JSONObject bestRouteLeg = bestRouteLegs.getJSONObject(0);

                int duration = leg.getJSONObject("duration").getInt("value");
                int bestRouteDuration = bestRouteLeg.getJSONObject("duration").getInt("value");

                // 比较逻辑，例如比较总时间
                if (duration < bestRouteDuration) {
                    bestRoute = route;
                }
            }
        }
        return bestRoute;
    }

    // 解碼polyline字符串
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void dialogSet(Context context, Marker marker) {
        String markerEscooterId = marker.getTitle();

        ViewStub stub = inflateViewStub(context, R.layout.component_menu_rent_info);
        ComponentMenuRentInfoBinding rentInfoBinding = ComponentMenuRentInfoBinding.bind(view);
        markerEscooterInfo(rentInfoBinding, markerEscooterId);


        rentInfoBinding.rentButton.setOnClickListener(v ->{
            //清空google map上的標記
            googleMap.clear();
            //停止引導locationCallback
            stopLocationUpdates();
            //EscooterService固定取得車輛gps
            escooterService = new EscooterService(rentViewModel);
            escooterService.startGpsUpdates();

            //ViewStub彈窗新增
            inflateNewViewStub(context, stub, R.layout.component_menu_scooter_info);
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


    private void rentEscooterInfo(ComponentMenuScooterInfoBinding binding,String markerEscooterId) {
        rentViewModel.rentEscooter();
        for (Escooter escooter : escooterList) {
            binding.scooterId.setText(markerEscooterId);
            binding.scooterModel.setText(escooter.getModelId());
            binding.batteryTimeText.setText(String.valueOf(escooter.getBatteryLevel()));
            binding.feePerMin.setText(String.valueOf(escooter.getFeePerMinutes()));
        }
    }
    private void updataRentEscooterInfo(ComponentMenuScooterInfoBinding binding) {
        binding.escooterRentTime.setText(escooterService.getStartTime());
        binding.duration.setText(String.valueOf(escooterService.getDuration()));
        binding.totalFee.setText(String.valueOf(escooterService.getTotalCost()));
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

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
        );
        // 取消導航請求
        if (navigationThread != null && navigationThread.isAlive()) {
            navigationThread.interrupt();
        }
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}