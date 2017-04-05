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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.SharedPref;
import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.ui.activity.MainActivity;
import ru.surfproject.app.weather.util.TimeUtils;
import ru.surfproject.app.weather.adapter.WeatherAdapter;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.model.Weather;
import ru.surfproject.app.weather.model.response.WeatherWeek;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private Observable<WeatherWeek> observableWeather;
    private Subscription subscriberWeather;
    private int countTest = 0;
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
            weatherDay = String.valueOf(listWeather.get(i).temp.morn.intValue()) + getString(R.string.signDegree);
            weatherNight = String.valueOf(listWeather.get(i).temp.night.intValue()) + getString(R.string.signDegree);
            Weather weather = new Weather();
            weather.setImage(weatherIcon);
            weather.setDay(dateFormat.format(date));
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).description));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);
            weather.setHumidity(String.valueOf(listWeather.get(i).humidity));
            weather.setPressure(String.valueOf(listWeather.get(i).pressure));
            weather.setWindSpeed(String.valueOf(listWeather.get(i).speed));
            weather.setDirection(String.valueOf(listWeather.get(i).deg));
            saveDateNow(TimeUtils.getDatetimeNow()); // Сохраняем данный момент времени в sharedPreference
            myWeather.add(weather);
        }
        // Запускаем лоудер, который записывает данные в БД
       // getActivity().getSupportLoaderManager().restartLoader(0, args(myWeather), setWeatherDBLoaderCallbacks1);
        saveWeatherToBD();
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
            observableWeather = App.getAPIServiceWeather().getWeatherCoord(lat, lon, cnt, units, lang, appid);
            subscriberWeather = observableWeather.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<WeatherWeek>() {
                        @Override
                        public void onCompleted() {
                            progressBar.setVisibility(View.GONE);
                            placeHolderNetwork.setVisibility(View.GONE);
                            if (refreshWeather.isRefreshing()) {
                                refreshWeather.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
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

                        @Override
                        public void onNext(WeatherWeek weatherWeek) {
                            toolbarCollapsing.setTitle(weatherWeek.city.name); // Устанавливаем имя города в титл город
                            setupWeather(weatherWeek.list); // Заполнение myWeather
                            mainRecyclerAdapter.updateWeatherList(myWeather);
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
                    // Метод получает погоду
                    getWeather();
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
        // Отписываемся от запроса, когда покидаем фрагмент
        if (subscriberWeather != null) {
            subscriberWeather.unsubscribe();
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
        if (requestCode == 1000) {
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
        // Как только получили координаты пользователя, выполняем сетевой запрос или получаем данные из БД
        getWeather();
        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }

    // Получение прогноза погоды, идёт выборка из БД или из сервака
    private void getWeather() {
        WeatherDao weatherDao = null;
        try {
            weatherDao = App.getHelper().getWeatherDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (weatherDao != null) {
            weatherDao.getAllWeather().subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Weather>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "Ошибка\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(List<Weather> weathers) {
                            //показываем полученные данные
                            myWeather.clear();
                            myWeather = weathers;
                            String dataNow = TimeUtils.getDatetimeNow();
                            String dataShared = getDateFromShared();
                            boolean isNecessaryUpdateWeather = TimeUtils.isNecessaryUpdateWeather(dataNow, dataShared);
                            if (isNecessaryUpdateWeather) {
                                // Необходимо обновить данные, так как прошло больше 2-х часов
                                getWeatherCoord(lat, lon, Const.CNT, Const.UTILS, Const.LANG, Const.WEATHER_API);
                            } else {
                                mainRecyclerAdapter.updateWeatherList(weathers);
                                progressBar.setVisibility(View.GONE);
                                placeHolderNetwork.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }
    private void saveWeatherToBD() {
        // Добавление в БД
        WeatherDao weatherDao = null;
        try {
            weatherDao = App.getHelper().getWeatherDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (weatherDao != null) {
                weatherDao.clearTable();
                for (int i = 0; i < myWeather.size(); i++) {
                    weatherDao.createOrUpdate(myWeather.get(i)); // Апдейтим или создаём
                }
            } else {
                Log.d("DATABASE", "weatherDao == null");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("DATABASE", "Ошибка:" + e.getMessage());
        }
    }
}
