package ru.surfproject.app.weather.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by devel on 09.02.2017.
 */

public class WeatherWeek {
    @SerializedName("city")
    @Expose
    public City city;
    @SerializedName("cod")
    @Expose
    public String cod;
    @SerializedName("message")
    @Expose
    public Double message;
    @SerializedName("cnt")
    @Expose
    public Integer cnt;
    @SerializedName("list")
    @Expose
    public java.util.List<ListWeather> list = null;

    public class ListWeather {

        @SerializedName("dt")
        @Expose
        public Integer dt;
        @SerializedName("temp")
        @Expose
        public Temp temp;
        @SerializedName("pressure")
        @Expose
        public Double pressure;
        @SerializedName("humidity")
        @Expose
        public Integer humidity;
        @SerializedName("weather")
        @Expose
        public java.util.List<Weather> weather = null;
        @SerializedName("speed")
        @Expose
        public Double speed;
        @SerializedName("deg")
        @Expose
        public Integer deg;
        @SerializedName("clouds")
        @Expose
        public Integer clouds;
        @SerializedName("snow")
        @Expose
        public Double snow;
        @SerializedName("rain")
        @Expose
        public Double rain;

    }

    public class Temp {

        @SerializedName("day")
        @Expose
        public Double day;
        @SerializedName("min")
        @Expose
        public Double min;
        @SerializedName("max")
        @Expose
        public Double max;
        @SerializedName("night")
        @Expose
        public Double night;
        @SerializedName("eve")
        @Expose
        public Double eve;
        @SerializedName("morn")
        @Expose
        public Double morn;

    }

    public class Weather {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("main")
        @Expose
        public String main;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("icon")
        @Expose
        public String icon;

    }

    public class City {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("coord")
        @Expose
        public Coord coord;
        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("population")
        @Expose
        public Integer population;

    }

    public class Coord {

        @SerializedName("lon")
        @Expose
        public Double lon;
        @SerializedName("lat")
        @Expose
        public Double lat;

    }

}
