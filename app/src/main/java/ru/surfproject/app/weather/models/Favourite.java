package ru.surfproject.app.weather.models;

/**
 * Created by ПК on 12/5/2016.
 */

public class Favourite {
    private String city;                //навзание города

    public Favourite(String city) {
        this.city = city;
    }

    public String getCity() { return city; }
}
