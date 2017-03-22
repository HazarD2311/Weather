package ru.surfproject.app.weather.db;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.sql.SQLException;
import java.util.List;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.model.Weather;

/**
 * Created by pkorl on 18.03.2017.
 */

public class GetWeatherDBLoader extends AsyncTaskLoader<List<Weather>> {

    private List<Weather> weathers;

    public GetWeatherDBLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        //запуск Loader'а
        if (weathers != null) {
            //используем кэшированные данные
            deliverResult(weathers); // Вызываем сразу метод, который показывает данные, миную их загрузку, так как они уже были когда-то загружены
        } else {
            //данных нет, их следует загрузить
            forceLoad(); //Метод запускает выполнение метода loadInBackground() в фотовом потоке
        }
    }

    @Override
    public List<Weather> loadInBackground() {
        WeatherDao weatherDao = null;
        try {
            weatherDao = App.getHelper().getWeatherDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (weatherDao != null) {
                return weatherDao.getAllWeather();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(List<Weather> weathers) {
        //доставка новых данных клиенту
        this.weathers = weathers;
        super.deliverResult(weathers);

    }
}
