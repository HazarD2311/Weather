package ru.surfproject.app.weather.Models;

/**
 * Created by pkorl on 04.12.2016.
 */

public class MainItemsRecyclerAdapter {
    final int image;
    final String day;
    final String typeWeather;
    final String temperature;

    public MainItemsRecyclerAdapter(int image, String day, String typeWeather, String temperature) {
        this.image = image;
        this.day = day;
        this.typeWeather = typeWeather;
        this.temperature = temperature;
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

    public String getTemperature() {
        return temperature;
    }
}
