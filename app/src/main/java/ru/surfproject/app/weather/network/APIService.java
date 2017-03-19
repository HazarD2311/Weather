package ru.surfproject.app.weather.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.surfproject.app.weather.models.response.WeatherWeek;
import ru.surfproject.app.weather.models.response.city.City;

/**
 * Created by pkorl on 18.12.2016.
 */

public interface APIService {

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

    @GET("maps/api/place/autocomplete/json")
    Call<City> getCity(@Query("input") String inputStr,
                       @Query("types") String types,
                       @Query("key") String key);

}
