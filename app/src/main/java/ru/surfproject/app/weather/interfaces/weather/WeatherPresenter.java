package ru.surfproject.app.weather.interfaces.weather;

import java.util.List;

import ru.surfproject.app.weather.model.Weather;
import rx.Observable;

public interface WeatherPresenter {

    void showErrorPresenter(String error);
    void showResultPresenter(Observable<List<Weather>> weatherList);
    void getWeather(String lat, String lon);
    void getWeatherCoordServer(String lat, String lon);

    void onPause();
}
