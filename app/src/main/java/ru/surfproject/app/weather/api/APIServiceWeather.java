package ru.surfproject.app.weather.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.surfproject.app.weather.model.response.WeatherWeek;
import ru.surfproject.app.weather.model.response.city.City;

public interface APIServiceWeather {

    @GET("data/2.5/forecast/daily")
    Call<WeatherWeek> getWeatherCoord(@Query("lat") String lat,
                                      @Query("lon") String lon,
                                      @Query("cnt") String cnt,
                                      @Query("units") String units,
                                      @Query("lang") String lang,
                                      @Query("appid") String appid);

    @GET("data/2.5/forecast/daily")
    Call<WeatherWeek> getWeatherCityName(@Query("q") String cityName,
                                      @Query("cnt") String cnt,
                                      @Query("units") String units,
                                      @Query("lang") String lang,
                                      @Query("appid") String appid);

}
