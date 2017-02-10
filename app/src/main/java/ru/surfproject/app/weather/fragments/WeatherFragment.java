package ru.surfproject.app.weather.fragments;


import android.Manifest;
import android.app.Activity;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.adapters.WeatherAdapter;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.models.Weather;
import ru.surfproject.app.weather.models.response.WeatherWeek;
import ru.surfproject.app.weather.network.APIService;

/**
 * Created by pkorl on 03.12.2016.
 */

public class WeatherFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private APIService service;
    private RecyclerView recyclerViewWeather;
    private ProgressBar progressBarWeather;
    private LinearLayout layoutNetworkError;
    private Button btnNetworkError;
    private TextView tvError;
    private View viewRoot;
    private String lat;
    private String lon;
    private String cnt;
    private String units;
    private String appid;
    private Call<WeatherWeek> callWeather;


    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder builder;
    private Status statusLocation;
    private int ERROR_CODE;

    private int countTest = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewRoot == null) { // проверяем вьюшки фрагмента на null, чтобы не пересоздавать его, если он уже есть
            viewRoot = inflater.inflate(R.layout.fragment_weather, container, false);
            progressBarWeather = (ProgressBar) viewRoot.findViewById(R.id.progress_fragment_weather);
            layoutNetworkError = (LinearLayout) viewRoot.findViewById(R.id.layout_network_error);
            btnNetworkError = (Button) viewRoot.findViewById(R.id.btn_network_error);
            tvError = (TextView) viewRoot.findViewById(R.id.tv_error);
            btnNetworkError.setOnClickListener(clickBtnNetworkError);
            initRetrofit(); // Инициализируем Retrofit
            cnt = "10";
            units = "celsius";
            appid = Const.WEATHER_API;
            getPermissionLocation(); // Получаем разрешения приложению
        }

        return viewRoot;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptorAndTimeOut())
                .build();
        service = retrofit.create(APIService.class);
    }

    private void setupRecycler(View view, List<WeatherWeek.ListWeather> listWeather) {
        recyclerViewWeather = (RecyclerView) view.findViewById(R.id.recycler_view_main);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
        recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        WeatherAdapter mainRecyclerAdapter = new WeatherAdapter(valuesForRecycler(listWeather)); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
    }

    // Метод для заполнения recyclerViewWeather
    private List<Weather> valuesForRecycler(List<WeatherWeek.ListWeather> listWeather) {
        List<Weather> weatherArr = new ArrayList<>();
        String weatherDay;
        String weatherNight;
        Date date;

        for (int i = 0; i < listWeather.size(); i++) {
            date = new Date();
            date.setTime((long) listWeather.get(i).dt * 1000);

            String drawableName = "weather" + listWeather.get(i).weather.get(0).icon;
            //получаем из имени ресурса идентификатор картинки
            int weatherIcon = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMMM", Locale.forLanguageTag("ru"));
            weatherDay = String.valueOf(listWeather.get(i).temp.morn.intValue()) + getString(R.string.signDegree);
            weatherNight = String.valueOf(listWeather.get(i).temp.night.intValue()) + getString(R.string.signDegree);

            Weather weather = new Weather();
            weather.setImage(weatherIcon);
            weather.setDay(dateFormat.format(date));
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).description));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);

            weatherArr.add(weather);
        }
        return weatherArr;
    }

    private void getWeather(String lat, String lon, String cnt, String units, String appid) {
        if (lat == null || lon == null) {
            progressBarWeather.setVisibility(View.GONE);
            layoutNetworkError.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
        } else {
            //отправляем запрос
            callWeather = service.getWeatherCoord(lat, lon, cnt, "metric", "ru", appid);
            callWeather.enqueue(new Callback<WeatherWeek>() {
                @Override
                public void onResponse(Call<WeatherWeek> call, Response<WeatherWeek> response) {
                    progressBarWeather.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body().city.name == null || response.body().list == null) {
                            ERROR_CODE = 1;
                            tvError.setText("Мы получили данные, но они с ошибкой");
                            btnNetworkError.setText("Повторить");
                            progressBarWeather.setVisibility(View.GONE);
                            layoutNetworkError.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Данные прогноза погоды не некорректные!", Toast.LENGTH_SHORT).show();
                        } else {
                            getActivity().setTitle(response.body().city.name); // Устанавливаем имя города в титл город
                            setupRecycler(viewRoot, response.body().list); // Заполнение recyclerViewWeather
                        }
                    }
                    progressBarWeather.setVisibility(View.GONE);
                    layoutNetworkError.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<WeatherWeek> call, Throwable t) {
                    ERROR_CODE = 1;
                    progressBarWeather.setVisibility(View.GONE);
                    layoutNetworkError.setVisibility(View.VISIBLE);
                    tvError.setText("Сетевой запрос выполнился с ошибкой");
                    btnNetworkError.setText("Повторить");
                    Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private View.OnClickListener clickBtnNetworkError = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            layoutNetworkError.setVisibility(View.GONE);
            progressBarWeather.setVisibility(View.VISIBLE);
            switch (ERROR_CODE) {
                case 0:
                    // Не предоставлены разрешения приложению
                    getPermissionLocation();
                    break;
                case 1:
                    // Сетевой запрос на получение погоды завершился с ошибкой
                    getWeather(lat, lon, cnt, units, appid);
                    break;
                case 2:
                    // Не включена геолокация
                    buildGoogleApiClient();
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (callWeather != null) {
            callWeather.cancel(); // Отменяем запрос, если фрагмент не в фокусе пользователя
        }
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

    /*-------------------------------------------------*/
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
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
                        progressBarWeather.setVisibility(View.GONE);
                        layoutNetworkError.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
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
        //TODO почему-то постоянно(бесконечно) выполняется этот метод, после того, как мы предоставили доступ к геопозиции устройства приложению,
        //TODO при последующих запусках такого не происходит.
        //TODO Пока стоит костыль
        countTest++;

        lastLocation = location;
        //Как только получили координаты, показываем погоду, скрываем прогрессбар
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        // Как только получили координаты пользователя, выполняем сетевой запрос
        if (countTest == 1) {
            getWeather(lat, lon, cnt, units, appid);
        }

        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
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
                    }

                } else {
                    // Если пользователь отказал в доступе
                    ERROR_CODE = 0;
                    progressBarWeather.setVisibility(View.GONE);
                    layoutNetworkError.setVisibility(View.VISIBLE);
                    tvError.setText("Разрешения не предоставлены");
                    btnNetworkError.setText("Предоставить");
                    Toast.makeText(getActivity(), "Разрешения не предоставлены!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // Тут можно добавить еще пермишены.
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                // Пользователь включил геолокацию
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Пользователь не включил геолокацию
                ERROR_CODE = 2;
                progressBarWeather.setVisibility(View.GONE);
                layoutNetworkError.setVisibility(View.VISIBLE);
                tvError.setText("Не включена геолокация");
                btnNetworkError.setText("Включить");
            }
        }
    }
}
