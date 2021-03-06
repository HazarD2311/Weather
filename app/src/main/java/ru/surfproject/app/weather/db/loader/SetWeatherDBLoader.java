package ru.surfproject.app.weather.db.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.db.DBHelper;
import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.model.Weather;

public class SetWeatherDBLoader extends AsyncTaskLoader<Integer> {

    @SuppressWarnings("unchecked")
    private List<Weather> weathers;

    public SetWeatherDBLoader(Context context, Bundle args) {
        super(context);
        weathers = (List<Weather>) args.getSerializable(Const.BUNDLE_WEATHER);
    }

    @Override
    protected void onStartLoading() {
        //запуск Loader'а
        if (weathers != null) {
            forceLoad(); //Метод запускает выполнение метода loadInBackground() в фотовом потоке
        }
    }

    @Override
    public Integer loadInBackground() {
        int k = 0;
        // Добавление в БД
        DBHelper helper = App.getHelper();
        WeatherDao weatherDao = null;
        try {
            weatherDao = helper.getWeatherDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (weatherDao != null) {
                //TODO очищаем таблицу, почему-то данные не обновляются, а постоянно добавляются
                weatherDao.clearTable();
                for (int i = 0; i < weathers.size(); i++) {
                    weatherDao.createOrUpdate(weathers.get(i)); // Апдейтим или создаём
                    k = i;
                }
                return k;
            } else {
                Log.d("DATABASE", "weatherDao == null");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("DATABASE", "Ошибка:" + e.getMessage());
        }
        return k;
    }

    @Override
    public void deliverResult(Integer count) {
        //доставка новых данных клиенту
        super.deliverResult(count);

    }
}