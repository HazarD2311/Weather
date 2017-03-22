package ru.surfproject.app.weather.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.surfproject.app.weather.model.Weather;

/**
 * Created by pkorl on 18.03.2017.
 */

public class WeatherDao extends BaseDaoImpl<Weather, Integer> {

    protected WeatherDao(ConnectionSource connectionSource, Class<Weather> weatherClass) throws SQLException {
        super(connectionSource, weatherClass);
    }

    public List<Weather> getAllWeather() throws SQLException {
        return this.queryForAll();
    }

    public void clearTable() {
        try {
            TableUtils.clearTable(connectionSource, Weather.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
