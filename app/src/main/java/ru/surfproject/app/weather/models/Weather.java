package ru.surfproject.app.weather.models;

/**
 * Created by pkorl on 04.12.2016.
 */

public class Weather {
    final int image;
    final String day;
    final String typeWeather;
    final String temperature1;
    final String temperature2;

    public Weather(int image, String day, String typeWeather, String temperature1, String temperature2) {
        this.image = image;
        this.day = day;
        this.typeWeather = typeWeather;
        this.temperature1 = temperature1;
        this.temperature2 = temperature2;
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

    public String getTemperature1() {
        return temperature1;
    }

    public String getTemperature2() {
        return temperature2;
    }
}
