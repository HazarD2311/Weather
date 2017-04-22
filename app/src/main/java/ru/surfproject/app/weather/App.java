package ru.surfproject.app.weather;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surfproject.app.weather.api.APIServiceGoogle;
import ru.surfproject.app.weather.api.APIServiceWeather;
import ru.surfproject.app.weather.db.DBHelper;

public class App extends Application {

    private static Retrofit retrofitOpenWeather;
    private static Retrofit retrofitGoogleAPI;
    private static APIServiceWeather serviceOpenWeather;
    private static APIServiceGoogle serviceGoogle;
    private static DBHelper helper;

    public static DBHelper getHelper() {
        return helper;
    }

    public static APIServiceWeather getInstanceServiceWeather() {
        if (serviceOpenWeather == null) {
            if (retrofitOpenWeather == null) {
                retrofitOpenWeather = new retrofit2.Retrofit.Builder()
                        .baseUrl(Const.BASE_URL)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(interceptorAndTimeOut())
                        .build();
            }
            serviceOpenWeather = retrofitOpenWeather.create(APIServiceWeather.class);
        }
        return serviceOpenWeather;
    }

    public static APIServiceGoogle getInstanceServiceGoogle() {
        if (serviceGoogle == null) {
            if (retrofitGoogleAPI == null) {
                retrofitGoogleAPI = new retrofit2.Retrofit.Builder()
                        .baseUrl(Const.BASE_URL_GOOGLE)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(interceptorAndTimeOut())
                        .build();
            }
            serviceGoogle = retrofitGoogleAPI.create(APIServiceGoogle.class);
        }
        return serviceGoogle;
    }

    // Метод возвращает объект OkHttpClient
    // В методе задаём логгирование и Timeout сетевого запросов
    private static OkHttpClient interceptorAndTimeOut() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DBHelper(this);
        SharedPref.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}