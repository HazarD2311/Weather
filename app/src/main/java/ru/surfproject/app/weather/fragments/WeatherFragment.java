package ru.surfproject.app.weather.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.adapters.WeatherAdapter;
import ru.surfproject.app.weather.models.Weather;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.models.weatherresponse.ListWeather;
import ru.surfproject.app.weather.models.weatherresponse.WeatherResponse;
import ru.surfproject.app.weather.network.APIService;

/**
 * Created by pkorl on 03.12.2016.
 */

public class WeatherFragment extends Fragment {

    private APIService service;
    private RecyclerView recyclerViewWeather;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initRetrofit(); // Инициализируем Retrofit
        Bundle bundle = getArguments();
        if (bundle != null){
            String lat = bundle.getString("latitude");
            String lon = bundle.getString("longitude");
            String cnt = "7";
            String units = "celsius";
            String appid = Const.WEATHER_API;
            Toast.makeText(getContext(), lat+", "+lon, Toast.LENGTH_SHORT).show();
            getWeather(view, lat, lon, cnt, units, appid);
        }
        return view;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(APIService.class);
    }

    private void setupRecycler(View view, List<ListWeather> listWeather) {
        recyclerViewWeather = (RecyclerView) view.findViewById(R.id.recycler_view_main);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
        recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        WeatherAdapter mainRecyclerAdapter = new WeatherAdapter(valuesForRecycler(listWeather)); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
    }

    // Метод для заполнения recyclerViewWeather
    private List<Weather> valuesForRecycler(List<ListWeather> listWeather) {
        List<Weather> test = new ArrayList<>();
        String weather1;
        String weather2;
        for (int i = 0; i < listWeather.size(); i++) {
            weather1 = String.valueOf(listWeather.get(i).temp.morn) + getString(R.string.signDegree);
            weather2 = String.valueOf(listWeather.get(i).temp.night) + getString(R.string.signDegree);
            Date date = new Date ();
            date.setTime((long)listWeather.get(i).dt*1000);
            test.add(new Weather(R.drawable.icon, date.toString(), String.valueOf(listWeather.get(i).weather.get(0).main), weather1, weather2));
        }
        return test;
    }

    private void getWeather(final View view, String lat, String lon, String cnt, String units, String appid) {
            //отправляем запрос
            Call<WeatherResponse> call = service.getWeatherCoord(lat, lon, cnt ,"metric", "ru" ,appid);
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if(response.isSuccessful()){
                        getActivity().setTitle(response.body().city.name); // Устанавливаем имя города в титл город
                        setupRecycler(view, response.body().list); // Заполнение recyclerViewWeather
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {

                }
            });
    }
}
