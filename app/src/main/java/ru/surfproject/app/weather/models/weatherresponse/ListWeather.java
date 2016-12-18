
package ru.surfproject.app.weather.models.weatherresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListWeather {

    @SerializedName("dt")
    @Expose
    public Long dt;
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
    @SerializedName("rain")
    @Expose
    public Double rain;
    @SerializedName("snow")
    @Expose
    public Double snow;
}
