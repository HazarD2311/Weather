package ru.surfproject.app.weather.models;

import android.util.MonthDisplayHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by pkorl on 04.12.2016.
 */

public class Weather {
    private int image;
    private String day;
    private String typeWeather;
    private String temperatureDay;
    private String temperatureNight;
    private String humidity;
    private String pressure;
    private String windSpeed;
    private String windDirection;

    public Weather() {

    }

    public Weather(int image, String day, String typeWeather, String temperatureDay, String temperatureNight, String humidity, String pressure, String windSpeed, String windDirection) {
        this.image = image;
        this.day = day;
        this.typeWeather = typeWeather;
        this.temperatureDay = temperatureDay;
        this.temperatureNight = temperatureNight;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    public int getImage() {
        return image;
    }

    public String getDay() {
        return day;
    }

    public String getTypeWeather() {
        return typeWeather;
    }

    public String getTemperatureDay() {
        return temperatureDay;
    }

    public String getTemperatureNight() {
        return temperatureNight;
    }

    public String getHumidity() {
        return "Влажность: " + humidity;
    }

    public String getPressure() {
        return "Давление: " + pressure;
    }

    public String getWindSpeed() {
        return "Скорость ветра: " + windSpeed;
    }

    public String getWindDirection() {
        return "Направление ветра: " + windDirection+"°";
    }

}
