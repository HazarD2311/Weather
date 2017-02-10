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
    private Locale locale;

    public Weather() {

    }

    public Weather(int image, String day, String typeWeather, String temperatureDay, String temperatureNight) {
        this.image = image;
        this.day = day;
        this.typeWeather = typeWeather;
        this.temperatureDay = temperatureDay;
        this.temperatureNight = temperatureNight;
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

    public void setImage(int image) {
        this.image = image;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTypeWeather(String typeWeather) {
        this.typeWeather = typeWeather;
    }

    public void setTemperatureDay(String temperatureDay) {
        this.temperatureDay = temperatureDay;
    }

    public void setTemperatureNight(String temperatureNight) {
        this.temperatureNight = temperatureNight;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
