package ru.surfproject.app.weather.interfaces.weather;

import java.util.List;

import ru.surfproject.app.weather.model.Weather;


public interface WeatherView {

    void showError(String error);
    void showResult(List<Weather> weatherList);
    void showProgress(boolean flag);

}
