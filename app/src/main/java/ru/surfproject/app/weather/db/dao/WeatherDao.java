package ru.surfproject.app.weather.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import ru.surfproject.app.weather.model.Weather;
import rx.Observable;

public class WeatherDao extends BaseDaoImpl<Weather, Integer> {

    public WeatherDao(ConnectionSource connectionSource, Class<Weather> weatherClass) throws SQLException {
        super(connectionSource, weatherClass);
    }

    /*  public List<Weather> getAllWeather() throws SQLException {
        return this.queryForAll();
    }*/
    public Observable<List<Weather>> getAllWeather() {
        return Observable.fromCallable(new Callable<List<Weather>>() {
            public List<Weather> call() throws Exception {
                return WeatherDao.this.queryForAll();
            }
        });
    }

    public void clearTable() {
        try {
            TableUtils.clearTable(connectionSource, Weather.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
