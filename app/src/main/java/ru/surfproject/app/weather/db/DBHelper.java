package ru.surfproject.app.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.model.Weather;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    protected WeatherDao weatherDao = null;

    private static final String DATABASE_NAME = "WeatherApp";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Weather.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Weather.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public WeatherDao getWeatherDao() throws SQLException {
        if (weatherDao == null) {
            weatherDao = new WeatherDao(getConnectionSource(), Weather.class); // Получаем WeatherDao
        }
        return weatherDao;
    }

    @Override
    public void close() {
        weatherDao = null;
        super.close();
    }
}
