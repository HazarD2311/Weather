package ru.surfproject.app.weather.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.models.response.WeatherWeek;
import ru.surfproject.app.weather.network.APIService;

/**
 * Created by pkorl on 27.11.2016.
 */

public class SearchMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap map;
    private MapView mapView;
    private Location lastLocation; // Координаты пользователя
    private Marker currLocationMarker;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;
    private Status statusLocation;
    private View view;

    private APIService service;
    private String nameCity;
    private String temp;
    private WeatherWindowAdapter weatherWindowAdapter;
    private ProgressBar progressWindowsInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        progressWindowsInfo = (ProgressBar) view.findViewById(R.id.progress_windows_info);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (mapView != null) {
            mapView.getMapAsync(this);
        }
        initRetrofit();
        return view;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptorAndTimeOut())
                .build();
        service = retrofit.create(APIService.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        getPermissionLocation(); // Получаем разрешения приложению
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }
        //Отключаем возможность кликать по маркеру
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        // Инициализируем кастомный InfoWindowAdapter
        weatherWindowAdapter = new WeatherWindowAdapter(getActivity().getLayoutInflater());
        map.setInfoWindowAdapter(weatherWindowAdapter);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        map.clear(); // Очищаем карду от маркеров
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng)); // Наводим камеру на место клика
        currLocationMarker = map.addMarker(new MarkerOptions().position(latLng)); // Добавляем маркер
        //Тестовый запрос на погоду
        getWeather(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), "1", "celsius", Const.WEATHER_API);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        // builder.setNeedBle(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                statusLocation = result.getStatus();
                switch (statusLocation.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Геопозиционирование включено
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Местоположение выключено, запускаем диалог с просьбой включить
                        try {
                            statusLocation.startResolutionForResult(getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Игнорируем ошибки
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Изменить параметры не получилось
                        break;
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        //устанавливаем маркер
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currLocationMarker = map.addMarker(markerOptions);

        //Двигаем камеру на место, где расположено устройство
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));

        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        currLocationMarker.showInfoWindow(); // Делаем сразу видимой панель над маркером
        // Тут делаем сетевой запрос, который возратит инфу о погоде
        getWeather(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), "1", "celsius", Const.WEATHER_API);
    }

    // Метод получает инфу о том, предоставил ли пользователь резрешение ACCESS_FINE_LOCATION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение было одобрено.
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }

                } else {
                    // Если пользователь отказал в доступе
                    Toast.makeText(getActivity(), "Разрешения не предоставлены!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // Тут можно добавить еще пермишены.
        }
    }

    public boolean checkLocationPermissionFragment() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Запрашиваем доступ к ACCESS_FINE_LOCATION
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Const.MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Const.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void getPermissionLocation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermissionFragment(); // Просим доступ к местоположению устройства.
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // Проверяем наличаем разрешения
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();

        }
    }

    private void getWeather(String lat, String lon, String cnt, String units, String appid) {
        progressWindowsInfo.setVisibility(View.VISIBLE);
        //отправляем запрос
        Call<WeatherWeek> callWeather = service.getWeatherCoord(lat, lon, cnt, "metric", "ru", appid);
        callWeather.enqueue(new Callback<WeatherWeek>() {
            @Override
            public void onResponse(Call<WeatherWeek> call, Response<WeatherWeek> response) {
                if (response.isSuccessful()) {
                    // Тут получили данные, сохраняем их и выводим в InfoWindow
                    progressWindowsInfo.setVisibility(View.GONE);
                    nameCity = String.valueOf(response.body().city.name);
                    temp = String.valueOf(response.body().list.get(0).temp.day);
                    weatherWindowAdapter.setNameCity(nameCity);
                    weatherWindowAdapter.setTemp(temp);
                    currLocationMarker.showInfoWindow(); // Делаем видимой панель над маркером (соответственно она инициализируется)
                }
            }

            @Override
            public void onFailure(Call<WeatherWeek> call, Throwable t) {
                progressWindowsInfo.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод возвращает объект OkHttpClient
    // В методе задаём логгирование и Timeout сетевого запросов
    private OkHttpClient interceptorAndTimeOut() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    public class WeatherWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private LayoutInflater inflater = null;
        private String nameCity;
        private String temp;

        public WeatherWindowAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return (null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            View popup = inflater.inflate(R.layout.marker_info, null);
            TextView tv = (TextView) popup.findViewById(R.id.tv_name_city);
            tv.setText(nameCity);
            tv = (TextView) popup.findViewById(R.id.tv_temp);
            tv.setText(temp);
            return (popup);
        }

        public void setNameCity(String nameCity) {
            this.nameCity = nameCity;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }
    }
}
