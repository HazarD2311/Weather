package ru.surfproject.app.weather.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.surfproject.app.weather.model.response.city.City;
import rx.Observable;

public interface APIServiceGoogle {

    @GET("maps/api/place/autocomplete/json")
    Observable<City> getCity(@Query("input") String inputStr,
                             @Query("types") String types,
                             @Query("key") String key);
}
