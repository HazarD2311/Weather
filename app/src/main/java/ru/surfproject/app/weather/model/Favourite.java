package ru.surfproject.app.weather.model;

/**
 * Created by ПК on 12/5/2016.
 */

public class Favourite {
    private String city; //название города

    public Favourite(String city) {
        this.city = city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}
