package ru.surfproject.app.weather.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.model.response.WeatherWeek;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchMapFragment extends FragmentLocation implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap map;
    private MapView mapView;
    private Marker currLocationMarker;
    private View view;
    private String nameCity;
    private String temp;
    private WeatherWindowAdapter weatherWindowAdapter;
    private ProgressBar progressWindowsInfo;
    private Observable<WeatherWeek> observableWeatherMap;
    private Subscription subscriberWeatherMap;

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
        return view;
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
        if (subscriberWeatherMap != null) {
            subscriberWeatherMap.unsubscribe();
        }
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

    private boolean checkLocationPermissionFragment() {
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
        subscriberWeatherMap = observableWeatherMap(lat, lon, cnt, units, appid)
                .subscribe(new Subscriber<WeatherWeek>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressWindowsInfo.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(WeatherWeek weatherWeek) {
                        // Тут получили данные, сохраняем их и выводим в InfoWindow
                        progressWindowsInfo.setVisibility(View.GONE);
                        nameCity = String.valueOf(weatherWeek.city.name);
                        temp = String.valueOf(weatherWeek.list.get(0).temp.day);
                        weatherWindowAdapter.setNameCity(nameCity);
                        weatherWindowAdapter.setTemp(temp);
                        currLocationMarker.showInfoWindow(); // Делаем видимой панель над маркером (соответственно она инициализируется)
                    }
                });
    }

    private Observable<WeatherWeek> observableWeatherMap(String lat, String lon, String cnt, String units, String appid) {
        return App.getInstanceServiceWeather().getWeatherCoord(lat, lon, cnt, "metric", "ru", appid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
