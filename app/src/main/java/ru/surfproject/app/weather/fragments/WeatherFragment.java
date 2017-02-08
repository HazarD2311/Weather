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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private ProgressBar progressBarWeather;
    private LinearLayout layoutNetworkError;
    private Button btnNetworkError;
    private View viewRoot;
    private String lat;
    private String lon;
    private String cnt;
    private String units;
    private String appid;
    private Call<WeatherResponse> callWeather;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewRoot == null) { // проверяем вьюшки фрагмента на null, чтобы не пересоздавать его, если он уже есть
            viewRoot = inflater.inflate(R.layout.fragment_weather, container, false);
            progressBarWeather = (ProgressBar) viewRoot.findViewById(R.id.progress_fragment_weather);
            layoutNetworkError = (LinearLayout) viewRoot.findViewById(R.id.layout_network_error);
            btnNetworkError = (Button) viewRoot.findViewById(R.id.btn_network_error);
            btnNetworkError.setOnClickListener(clickBtnNetworkError);
            initRetrofit(); // Инициализируем Retrofit
            Bundle bundle = getArguments();
            if (bundle != null) {
                lat = bundle.getString("latitude");
                lon = bundle.getString("longitude");
                cnt = "7";
                units = "celsius";
                appid = Const.WEATHER_API;
                getWeather(lat, lon, cnt, units, appid);
            }
        }
        return viewRoot;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptorAndTimeOut())
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
        String weatherDay;
        String weatherNight;
        for (int i = 0; i < listWeather.size(); i++) {
            weatherDay = String.valueOf(listWeather.get(i).temp.morn.intValue()) + getString(R.string.signDegree);
            weatherNight = String.valueOf(listWeather.get(i).temp.night.intValue()) + getString(R.string.signDegree);
            Date date = new Date(listWeather.get(i).dt * 1000);

            String drawableName = "weather" + listWeather.get(i).weather.get(0).icon;
            //получаем из имени ресурса идентификатор картинки
            int weatherIcon = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());

            Weather weather = new Weather();
            weather.setImage(weatherIcon);
            weather.setDay(date.getDay(), date.getDate(), date.getMonth());
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).main));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);

            test.add(weather);
        }
        return test;
    }

    private void getWeather(String lat, String lon, String cnt, String units, String appid) {
        if (lat == null || lon == null) {
            progressBarWeather.setVisibility(View.GONE);
            layoutNetworkError.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
        } else {
            //отправляем запрос
            callWeather = service.getWeatherCoord(lat, lon, cnt, "metric", "ru", appid);
            callWeather.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    progressBarWeather.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body().city.name == null || response.body().list == null) {
                            progressBarWeather.setVisibility(View.GONE);
                            layoutNetworkError.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Данные прогноза погоды не некорректные!", Toast.LENGTH_SHORT).show();
                        } else {
                            getActivity().setTitle(response.body().city.name); // Устанавливаем имя города в титл город
                            setupRecycler(viewRoot, response.body().list); // Заполнение recyclerViewWeather
                        }
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    progressBarWeather.setVisibility(View.GONE);
                    layoutNetworkError.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "Ошибка загрузки погоды!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private View.OnClickListener clickBtnNetworkError = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            layoutNetworkError.setVisibility(View.GONE);
            progressBarWeather.setVisibility(View.VISIBLE);
            getWeather(lat, lon, cnt, units, appid);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (callWeather != null) {
            callWeather.cancel(); // Отменяем запрос, если фрагмент не в фокусе пользователя
        }
    }

    // Метод возвращает объект OkHttpClient
    // В методе задаём логгирование и Timeout сетевого запросов
    private OkHttpClient interceptorAndTimeOut() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }
}
