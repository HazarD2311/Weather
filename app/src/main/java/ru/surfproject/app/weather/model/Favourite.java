package ru.surfproject.app.weather.model;

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
