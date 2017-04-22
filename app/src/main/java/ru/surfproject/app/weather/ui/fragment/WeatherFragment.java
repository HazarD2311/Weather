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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.SharedPref;
import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.interfaces.weather.WeatherPresenter;
import ru.surfproject.app.weather.interfaces.weather.WeatherView;
import ru.surfproject.app.weather.presenter.WeatherPresenterImpl;
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
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeatherFragment extends FragmentLocation implements WeatherView {

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
    private String mLat;
    private String mLon;
    private Toolbar toolbarCollapsing;
    private SwipeRefreshLayout refreshWeather;
    private ErrorCode errorCode;
    private WeatherAdapter mainRecyclerAdapter;
    private WeatherPresenter presenter;

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
                presenter.getWeatherCoordServer(mLat, mLon);
            }
        });
        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progress_fragment_weather);
        placeHolderNetwork = (LinearLayout) viewRoot.findViewById(R.id.layout_network_error);
        btnRepeatCommand = (Button) viewRoot.findViewById(R.id.btn_network_error);
        errorMessageTextView = (TextView) viewRoot.findViewById(R.id.tv_error);
        btnRepeatCommand.setOnClickListener(clickBtnNetworkError);
        getPermissionLocation(); // Получаем разрешения приложению
        presenter = new WeatherPresenterImpl(this, getContext());
        return viewRoot;
    }

    private void initRecyclerView() {
        recyclerViewWeather = (RecyclerView) viewRoot.findViewById(R.id.recycler_view_main);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
        recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        mainRecyclerAdapter = new WeatherAdapter(getContext()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
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
                    presenter.getWeather(mLat, mLon);
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
        presenter.onPause();
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
        if (requestCode == Const.PERMISSIONS_REQUEST_CODE) {
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
        mLat = String.valueOf(location.getLatitude());
        mLon = String.valueOf(location.getLongitude());
        // Как только получили координаты пользователя, выполняем сетевой запрос или получаем данные из БД
        presenter.getWeather(mLat, mLon);
        //Останавливаем обновление LocationServices
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }


    }

    @Override
    public void showError(String error) {
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
    public void showResult(List<Weather> weatherList) {
        progressBar.setVisibility(View.GONE);
        placeHolderNetwork.setVisibility(View.GONE);
        if (refreshWeather.isRefreshing()) {
            refreshWeather.setRefreshing(false);
        }
       // toolbarCollapsing.setTitle(weatherWeek.city.name); // Устанавливаем имя города в титл город
        mainRecyclerAdapter.updateWeatherList(weatherList);
    }

    @Override
    public void showProgress(boolean flag) {
        if (flag) {
            placeHolderNetwork.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            placeHolderNetwork.setVisibility(View.GONE);
            if (refreshWeather.isRefreshing()) {
                refreshWeather.setRefreshing(false);
            }
        }
    }
}
