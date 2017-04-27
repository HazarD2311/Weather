package ru.surfproject.app.weather.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.SharedPref;
import ru.surfproject.app.weather.model.Pressure;
import ru.surfproject.app.weather.model.Speed;
import ru.surfproject.app.weather.model.Temperature;
import ru.surfproject.app.weather.ui.activity.MainActivity;
import ru.surfproject.app.weather.util.TimeUtils;
import ru.surfproject.app.weather.adapter.WeatherAdapter;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.db.loader.GetWeatherDBLoader;
import ru.surfproject.app.weather.db.loader.SetWeatherDBLoader;
import ru.surfproject.app.weather.model.Weather;
import ru.surfproject.app.weather.model.response.WeatherWeek;

public class WeatherFragment extends FragmentLocation {

    enum ErrorCode {
        NO_PERMISSIONS,
        OK,
        NO_LOCATION;
    }

    private RecyclerView recyclerViewWeather;
    private ProgressBar progressBar;
    private LinearLayout placeHolderNetwork;
    private Button btnRepeatCommand;
    private TextView errorMessageTextView;
    private View viewRoot;
    private String lat;
    private String lon;
    private Call<WeatherWeek> callWeather;
    private Toolbar toolbarCollapsing;
    private SwipeRefreshLayout refreshWeather;
    private ErrorCode errorCode;
    private WeatherAdapter mainRecyclerAdapter;
    private List<Weather> myWeather = new ArrayList<>();

    public static Bundle args(List<Weather> listWeather) {
        Bundle args = new Bundle();
        args.putSerializable(Const.BUNDLE_WEATHER, (Serializable) listWeather);
        return args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.fragment_weather, container, false);
        initRecyclerView(); // Инициализация RecyclerView
        toolbarCollapsing = (Toolbar) viewRoot.findViewById(R.id.toolbar_collapsing);
        ((MainActivity) getActivity()).setSupportActionBar(toolbarCollapsing);
        refreshWeather = (SwipeRefreshLayout) viewRoot.findViewById(R.id.refresh_weather);
        refreshWeather.setEnabled(false);
        refreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherCoord(lat, lon, Const.CNT, Const.UTILS, Const.LANG, Const.WEATHER_API);
            }
        });
        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progress_fragment_weather);
        placeHolderNetwork = (LinearLayout) viewRoot.findViewById(R.id.layout_network_error);
        btnRepeatCommand = (Button) viewRoot.findViewById(R.id.btn_network_error);
        errorMessageTextView = (TextView) viewRoot.findViewById(R.id.tv_error);
        btnRepeatCommand.setOnClickListener(clickBtnNetworkError);
        getPermissionLocation(); // Получаем разрешения приложению
        return viewRoot;
    }

    private void initRecyclerView() {
        recyclerViewWeather = (RecyclerView) viewRoot.findViewById(R.id.recycler_view_main);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
        recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        mainRecyclerAdapter = new WeatherAdapter(myWeather, getContext()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
    }

    // Метод заполняет myWeather
    private void setupWeather(List<WeatherWeek.ListWeather> listWeather) {
        String weatherDay;
        String weatherNight;
        Date date;
        myWeather.clear();
        for (int i = 0; i < listWeather.size(); i++) {
            date = new Date();
            date.setTime((long) listWeather.get(i).dt * 1000);
            String drawableName = "weather" + listWeather.get(i).weather.get(0).icon;
            //получаем из имени ресурса идентификатор картинки
            int weatherIcon = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMMM", Locale.getDefault());
            weatherDay = setupTemperature(listWeather.get(i).temp.morn, SharedPref.getSharedPreferences().getString(Const.TYPE_OF_WEATHER, ""));
            weatherNight = setupTemperature(listWeather.get(i).temp.night, SharedPref.getSharedPreferences().getString(Const.TYPE_OF_WEATHER, ""));
            Weather weather = new Weather();
            weather.setImage(weatherIcon);
            weather.setDay(dateFormat.format(date));
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).description));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);
            weather.setHumidity(String.valueOf(listWeather.get(i).humidity));
            weather.setPressure(setupPressure(listWeather.get(i).pressure, SharedPref.getSharedPreferences().getString(Const.TYPE_OF_PRESSURE, "")));
            weather.setWindSpeed(setupSpeed(listWeather.get(i).speed, SharedPref.getSharedPreferences().getString(Const.TYPE_OF_SPEED, "")));
            weather.setDirection(String.valueOf(listWeather.get(i).deg));
            saveDateNow(TimeUtils.getDatetimeNow()); // Сохраняем данный момент времени в sharedPreference
            myWeather.add(weather);
        }
        // Запускаем лоудер, который записывает данные в БД
        getActivity().getSupportLoaderManager().restartLoader(0, args(myWeather), setWeatherDBLoaderCallbacks);
    }

    private String setupTemperature(Double temperatureValue, String typeFromSettings) {
        Temperature temperature = new Temperature(temperatureValue);
        switch (typeFromSettings) {
            case "1":
                return temperature.getIntCelsius() + getString(R.string.sign_degree);
            case "0":
                return temperature.getIntFahrenheit() + getString(R.string.sign_degree);
        }
        return "ошибка";
    }

    private String setupSpeed(Double speedValue, String typeFromSettings) {
        Speed speed = new Speed(speedValue);
        switch (typeFromSettings) {
            case "1":
                return speed.getMeterSec() + " " + getString(R.string.meter_sec);
            case "0":
                return speed.getKmHour() + " " + getString(R.string.km_hour);
            case "-1":
                return speed.getMileHour() + " " + getString(R.string.mile_hour);
        }
        return "ошибка";
    }

    private String setupPressure(Double pressureValue, String typeFromSettings) {
        Pressure pressure = new Pressure(pressureValue);
        switch (typeFromSettings) {
            case "1":
                return String.valueOf(pressure.gethPa());
            case "0":
                return String.valueOf(pressure.getMmRtSt());
        }
        return "ошибка";
    }

    private void saveDateNow(String dateNow) {
        SharedPreferences.Editor edit = SharedPref.getSharedPreferences().edit();
        edit.putString(Const.DATA_NOW, dateNow);
        edit.apply();
    }

    private String getDateFromShared() {
        return SharedPref.getSharedPreferences().getString(Const.DATA_NOW, "");
    }

    private void getWeatherCoord(String lat, String lon, String cnt, String units, String lang, String appid) {
        if (lat == null || lon == null) {
            progressBar.setVisibility(View.GONE);
            placeHolderNetwork.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
        } else {
            //отправляем запрос
            callWeather = App.getAPIServiceWeather().getWeatherCoord(lat, lon, cnt, units, lang, appid);
            callWeather.enqueue(new Callback<WeatherWeek>() {
                @Override
                public void onResponse(Call<WeatherWeek> call, Response<WeatherWeek> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body().city.name == null || response.body().list == null) {
                            errorCode = ErrorCode.OK;
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
                            refreshWeather.setEnabled(true);
                            toolbarCollapsing.setTitle(response.body().city.name); // Устанавливаем имя города в титл город
                            setupWeather(response.body().list); // Заполнение myWeather
                            mainRecyclerAdapter.updateWeatherList(myWeather);

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
                    errorCode = ErrorCode.OK;
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
            switch (errorCode) {
                case NO_PERMISSIONS:
                    // Не предоставлены разрешения приложению
                    getPermissionLocation();
                    break;
                case OK:
                    // Запускаем лоадер, который получает данные из БД
                    getActivity().getSupportLoaderManager().restartLoader(0, null, getWeatherDBLoaderCallbacks);
                    break;
                case NO_LOCATION:
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
                    errorCode = ErrorCode.NO_PERMISSIONS;
                    progressBar.setVisibility(View.GONE);
                    placeHolderNetwork.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText("Разрешения не предоставлены");
                    btnRepeatCommand.setText("Предоставить");
                    Toast.makeText(getActivity(), "Разрешения не предоставлены!", Toast.LENGTH_LONG).show();
                }
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
        if (requestCode == Const.REQUEST_CODE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                // Пользователь включил геолокацию
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Пользователь не включил геолокацию
                errorCode = ErrorCode.NO_LOCATION;
                progressBar.setVisibility(View.GONE);
                placeHolderNetwork.setVisibility(View.VISIBLE);
                errorMessageTextView.setText("Не включена геолокация");
                btnRepeatCommand.setText("Включить");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        //Как только получили координаты, показываем погоду, скрываем прогрессбар
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        // Как только получили координаты пользователя, выполняем сетевой запрос
        // Запускаем лоадер
        getActivity().getSupportLoaderManager().restartLoader(0, null, getWeatherDBLoaderCallbacks);

        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }

    // Коллбек лоудера setWeatherDBLoaderCallbacks, тут получаем данные погоды
    private LoaderManager.LoaderCallbacks<List<Weather>> getWeatherDBLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Weather>>() {
        @Override
        public Loader<List<Weather>> onCreateLoader(int id, Bundle args) {
            return new GetWeatherDBLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Weather>> loader, List<Weather> weather) {
            //показываем полученные данные
            myWeather.clear();
            myWeather = weather;
            String dataNow = TimeUtils.getDatetimeNow();
            String dataShared = getDateFromShared();
            boolean isNecessaryUpdateWeather = TimeUtils.isNecessaryUpdateWeather(dataNow, dataShared);
            if (isNecessaryUpdateWeather) {
                // Необходимо обновить данные, так как прошло больше 2-х часов
                getWeatherCoord(lat, lon, Const.CNT, Const.UTILS, Const.LANG, Const.WEATHER_API);
            } else {
                refreshWeather.setEnabled(true);
                mainRecyclerAdapter.updateWeatherList(weather);
                progressBar.setVisibility(View.GONE);
                placeHolderNetwork.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Weather>> loader) {
            //Loader перезапущен, очищаются все данные, unregister any listeners, etc.
        }
    };
    // Коллбек лоудера setWeatherDBLoaderCallbacks, тут приходит инфа о кол-ве записанных данных
    private LoaderManager.LoaderCallbacks<Integer> setWeatherDBLoaderCallbacks = new LoaderManager.LoaderCallbacks<Integer>() {
        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            return new SetWeatherDBLoader(getActivity(), args);
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer cout) {
            //показываем полученные данные
            Log.d("DATABASE", "Кол-во записей добавленых(обновленных) в БД: " + cout);
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {
            //Loader перезапущен, очищаются все данные, unregister any listeners, etc.
        }
    };
}
