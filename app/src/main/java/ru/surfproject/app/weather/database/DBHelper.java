package ru.surfproject.app.weather.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ru.surfproject.app.weather.models.Weather;

/**
 * Created by pkorl on 16.03.2017.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "WeatherApp";
    private static final int DATABASE_VERSION = 1;

    private Dao<Weather, Integer> weatherDao = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Weather.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Weather.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    /* User */

    public Dao<Weather, Integer> getWeatherDao() throws SQLException {
        if (weatherDao == null) {
            try {
                weatherDao = getDao(Weather.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        return weatherDao;
    }

    @Override
    public void close() {
        weatherDao = null;

        super.close();
    }
}
