package ru.surfproject.app.weather.models;

/**
 * Created by ПК on 12/5/2016.
 */

public class Favourite {
    private String city;                //навзание города
    private String averageTemperature;  //средняя температура на сегодня в данном городе

    public Favourite(String city, String averageTemperature) {
        this.city = city;
        this.averageTemperature = averageTemperature;
    }

    public String getCity() { return city; }
    public String getAverageTemperature() { return averageTemperature; }
}
