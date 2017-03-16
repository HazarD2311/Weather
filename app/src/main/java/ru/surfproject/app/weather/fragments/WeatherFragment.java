package ru.surfproject.app.weather.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.dao.Dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.FragmentLocation;
import ru.surfproject.app.weather.MainActivity;
import ru.surfproject.app.weather.RetrofitInit;
import ru.surfproject.app.weather.Utilities;
import ru.surfproject.app.weather.adapters.WeatherAdapter;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.database.DBHelper;
import ru.surfproject.app.weather.models.Weather;
import ru.surfproject.app.weather.models.response.WeatherWeek;

/**
 * Created by pkorl on 03.12.2016.
 */

public class WeatherFragment extends FragmentLocation {

    private RecyclerView recyclerViewWeather;
    private ProgressBar progressBar;
    private LinearLayout placeHolderNetwork;
    private Button btnRepeatCommand;
    private TextView errorMessageTextView;
    private View viewRoot;
    private String lat;
    private String lon;
    private String cnt;
    private String units;
    private String appid;
    private Call<WeatherWeek> callWeather;
    private int ERROR_CODE;
    private int countTest = 0;
    private RetrofitInit retrofitInit;
    private Toolbar toolbarCollapsing;
    private SwipeRefreshLayout refreshWeather;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragment_weather, container, false);

        toolbarCollapsing = (Toolbar) viewRoot.findViewById(R.id.toolbar_collapsing);
        ((MainActivity) getActivity()).setSupportActionBar(toolbarCollapsing);
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawer.addDrawerListener(toggle);
        // toggle.syncState();
        refreshWeather = (SwipeRefreshLayout) viewRoot.findViewById(R.id.refresh_weather);
        refreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeather(lat, lon, cnt, units, appid);
            }
        });

        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progress_fragment_weather);
        placeHolderNetwork = (LinearLayout) viewRoot.findViewById(R.id.layout_network_error);
        btnRepeatCommand = (Button) viewRoot.findViewById(R.id.btn_network_error);
        errorMessageTextView = (TextView) viewRoot.findViewById(R.id.tv_error);
        btnRepeatCommand.setOnClickListener(clickBtnNetworkError);
        retrofitInit = new RetrofitInit();
        retrofitInit.initRetrofit(); // Инициализируем RetrofitInit
        cnt = "10";
        units = "celsius";
        appid = Const.WEATHER_API;
        getPermissionLocation(); // Получаем разрешения приложению
        return viewRoot;
    }

    private void setupRecycler(View view, List<WeatherWeek.ListWeather> listWeather) {
        if (recyclerViewWeather == null) {
            recyclerViewWeather = (RecyclerView) view.findViewById(R.id.recycler_view_main);
            recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
            recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        }
        WeatherAdapter mainRecyclerAdapter = new WeatherAdapter(valuesForRecycler(listWeather), getContext()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
    }

    private void setupRecyclerExistWeather(View view, List<Weather> listWeather) {
        if (recyclerViewWeather == null) {
            recyclerViewWeather = (RecyclerView) view.findViewById(R.id.recycler_view_main);
            recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
            recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        }
        WeatherAdapter mainRecyclerAdapter = new WeatherAdapter(listWeather, getContext()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMMM", Locale.getDefault());
            weatherDay = String.valueOf(listWeather.get(i).temp.morn.intValue()) + getString(R.string.signDegree);
            weatherNight = String.valueOf(listWeather.get(i).temp.night.intValue()) + getString(R.string.signDegree);

            Weather weather = new Weather();
            weatherArr.add(weather);
            weather.setImage(weatherIcon);
            weather.setDay(dateFormat.format(date));
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).description));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);
            weather.setHumidity(String.valueOf(listWeather.get(i).humidity));
            weather.setPressure(String.valueOf(listWeather.get(i).pressure));
            weather.setWindSpeed(String.valueOf(listWeather.get(i).speed));
            weather.setDirection(String.valueOf(listWeather.get(i).deg));
            weather.setTime(Utilities.getDatetimeNow());


            // Добавление в БД
            DBHelper helper = new DBHelper(getContext());
            Dao<Weather, Integer> weatherDao = null;
            try {
                weatherDao = helper.getWeatherDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (weatherDao != null) {
                    weatherDao.createOrUpdate(weather); // Апдейтим или создаём
                    Log.d("DATABASE", "Погодо успешно добавлена в БД");
                } else {
                    Log.d("DATABASE", "weatherDao == null");
                }
            } catch (SQLException | java.sql.SQLException e) {
                e.printStackTrace();
                Log.d("DATABASE", "Ошибка:" + e.getMessage());
            }

        }
        return weatherArr;
    }


    private void getWeather(String lat, String lon, String cnt, String units, String appid) {
        if (lat == null || lon == null) {
            progressBar.setVisibility(View.GONE);
            placeHolderNetwork.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
        } else {
            //отправляем запрос
            callWeather = retrofitInit.service.getWeatherCoord(lat, lon, cnt, "metric", "ru", appid);
            callWeather.enqueue(new Callback<WeatherWeek>() {
                @Override
                public void onResponse(Call<WeatherWeek> call, Response<WeatherWeek> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body().city.name == null || response.body().list == null) {
                            ERROR_CODE = 1;
                            if (refreshWeather.isRefreshing()) {
                                refreshWeather.setRefreshing(false);
                            } else {
                                errorMessageTextView.setText("Мы получили данные, но они с ошибкой");
                                btnRepeatCommand.setText("Повторить");
                                progressBar.setVisibility(View.GONE);
                                placeHolderNetwork.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(getContext(), "Данные прогноза погоды не некорректные!", Toast.LENGTH_SHORT).show();
                        } else {
                            toolbarCollapsing.setTitle(response.body().city.name); // Устанавливаем имя города в титл город
                            setupRecycler(viewRoot, response.body().list); // Заполнение recyclerViewWeather
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    placeHolderNetwork.setVisibility(View.GONE);
                    if (refreshWeather.isRefreshing()) {
                        refreshWeather.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<WeatherWeek> call, Throwable t) {
                    ERROR_CODE = 1;
                    if (refreshWeather.isRefreshing()) {
                        refreshWeather.setRefreshing(false);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        placeHolderNetwork.setVisibility(View.VISIBLE);
                        errorMessageTextView.setText("Сетевой запрос выполнился с ошибкой");
                        btnRepeatCommand.setText("Повторить");
                    }
                    Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private View.OnClickListener clickBtnNetworkError = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            placeHolderNetwork.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            switch (ERROR_CODE) {
                case 0:
                    // Не предоставлены разрешения приложению
                    getPermissionLocation();
                    break;
                case 1:
                    List<Weather> weather = weatherFromBD();
                    if (weather.size() != 0) {
                        setupRecyclerExistWeather(viewRoot, weather); // Заполнение recyclerViewWeather
                        progressBar.setVisibility(View.GONE);
                        placeHolderNetwork.setVisibility(View.GONE);
                    } else {
                        // Сетевой запрос на получение погоды завершился с ошибкой
                        getWeather(lat, lon, cnt, units, appid);
                    }
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

    private List<Weather> weatherFromBD() {
        DBHelper helper = new DBHelper(getContext());
        Dao<Weather, Integer> weatherDao = null;
        try {
            weatherDao = helper.getWeatherDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (weatherDao != null) {
                return weatherDao.queryForAll();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                    progressBar.setVisibility(View.GONE);
                    placeHolderNetwork.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText("Разрешения не предоставлены");
                    btnRepeatCommand.setText("Предоставить");
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
                progressBar.setVisibility(View.GONE);
                placeHolderNetwork.setVisibility(View.VISIBLE);
                errorMessageTextView.setText("Не включена геолокация");
                btnRepeatCommand.setText("Включить");
            }
        }
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
            List<Weather> weather = weatherFromBD();
            if (weather.size() != 0) {
                setupRecyclerExistWeather(viewRoot, weather); // Заполнение recyclerViewWeather
                progressBar.setVisibility(View.GONE);
                placeHolderNetwork.setVisibility(View.GONE);
            } else {
                // Сетевой запрос на получение погоды завершился с ошибкой
                getWeather(lat, lon, cnt, units, appid);
            }
        }

        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

}
