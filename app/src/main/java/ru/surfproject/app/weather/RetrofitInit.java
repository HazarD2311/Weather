package ru.surfproject.app.weather;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surfproject.app.weather.network.APIService;

/**
 * Created by pkorl on 24.02.2017.
 */

public class RetrofitInit {

    public APIService service;

    public void initRetrofit() {
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptorAndTimeOut())
                .build();
        if (service == null) {
            service = retrofit.create(APIService.class);
        }
    }
    public void initRetrofit2() {
        Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(Const.BASE_URL_GOOGLE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(interceptorAndTimeOut())
                .build();
        if (service == null) {
            service = retrofit.create(APIService.class);
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
