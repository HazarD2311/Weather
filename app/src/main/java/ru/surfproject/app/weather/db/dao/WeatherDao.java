package ru.surfproject.app.weather.db.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import ru.surfproject.app.weather.model.Weather;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WeatherDao extends BaseDaoImpl<Weather, Integer> {

    public WeatherDao(ConnectionSource connectionSource, Class<Weather> weatherClass) throws SQLException {
        super(connectionSource, weatherClass);
    }

    public Observable<List<Weather>> getAllWeather() {
        return Observable.fromCallable(new Callable<List<Weather>>() {
            public List<Weather> call() throws Exception {
                Log.d("WeatherDao", "Thread getAllWeather: "+ Thread.currentThread().getName());
                return WeatherDao.this.queryForAll();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void addWeather(final List<Weather> weathers) throws SQLException {
        WeatherDao.this.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                clearTable();
                Log.d("WeatherDao", "Thread addWeather: "+ Thread.currentThread().getName());
                for (Weather weather : weathers) {
                    WeatherDao.this.create(weather);
                }
                return null;
            }
        });
    }

    private void clearTable() {
        try {
            Log.d("WeatherDao", "Thread clearTable: "+ Thread.currentThread().getName());
            TableUtils.clearTable(connectionSource, Weather.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
