package ru.surfproject.app.weather.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.surfproject.app.weather.models.weatherresponse.WeatherResponse;

/**
 * Created by pkorl on 18.12.2016.
 */

public interface APIService {

    @GET("data/2.5/forecast/daily")
    Call<WeatherResponse> getWeatherCoord(@Query("lat") String lat,
                                          @Query("lon") String lon,
                                          @Query("cnt") String cnt,
                                          @Query("units") String units,
                                          @Query("lang") String lang,
                                          @Query("appid") String appid);
}
