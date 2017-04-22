package ru.surfproject.app.weather.interactor;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.SharedPref;
import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.interfaces.weather.WeatherInteractor;
import ru.surfproject.app.weather.interfaces.weather.WeatherPresenter;
import ru.surfproject.app.weather.model.Weather;
import ru.surfproject.app.weather.model.response.WeatherWeek;
import ru.surfproject.app.weather.util.TimeUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class WeatherInteractorImpl implements WeatherInteractor {

    private WeatherPresenter presenter;
    private Context context;

    public WeatherInteractorImpl(WeatherPresenter presenter, Context context) {
        this.presenter = presenter;
        this.context = context;
    }


    public Observable<List<Weather>> getWeatherObservable(String lat, String lon, String cnt, String units, String lang, String appid) {
        return App.getInstanceServiceWeather().getWeatherCoord(lat, lon, cnt, units, lang, appid)
                .flatMap(new Func1<WeatherWeek, Observable<List<Weather>>>() {
                    @Override
                    public Observable<List<Weather>> call(WeatherWeek weatherWeek) {
                        List<Weather> listWeather = setupWeather(weatherWeek.list);
                        saveWeatherToBD(listWeather);
                        return Observable.just(listWeather);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void getWeatherInteractor(String lat, String lon, String cnt, String units, String lang, String appid) {
        presenter.showResultPresenter(getWeatherObservable(lat, lon, cnt, units, lang, appid));
    }

    private void saveWeatherToBD(List<Weather> weathers) {
        // Добавление в БД
        WeatherDao weatherDao;
        try {
            weatherDao = App.getHelper().getWeatherDao();
            if (weatherDao != null) {
                weatherDao.addWeather(weathers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Weather> setupWeather(List<WeatherWeek.ListWeather> listWeather) {
        List<Weather> myWeather = new ArrayList<>();
        String weatherDay;
        String weatherNight;
        int weatherIcon;
        String drawableName;
        Date date;
        for (int i = 0; i < listWeather.size(); i++) {
            date = new Date();
            date.setTime((long) listWeather.get(i).dt * 1000);
            drawableName = "weather" + listWeather.get(i).weather.get(0).icon;
            //получаем из имени ресурса идентификатор картинки
            weatherIcon = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMMM", Locale.getDefault());
            weatherDay = String.valueOf(listWeather.get(i).temp.morn.intValue()) + context.getString(R.string.signDegree);
            weatherNight = String.valueOf(listWeather.get(i).temp.night.intValue()) + context.getString(R.string.signDegree);
            Weather weather = new Weather();
            weather.setImage(weatherIcon);
            weather.setDay(dateFormat.format(date));
            weather.setTypeWeather(String.valueOf(listWeather.get(i).weather.get(0).description));
            weather.setTemperatureDay(weatherDay);
            weather.setTemperatureNight(weatherNight);
            weather.setHumidity(String.valueOf(listWeather.get(i).humidity));
            weather.setPressure(String.valueOf(listWeather.get(i).pressure));
            weather.setWindSpeed(String.valueOf(listWeather.get(i).speed));
            weather.setDirection(String.valueOf(listWeather.get(i).deg));
            saveDateNow(TimeUtils.getDatetimeNow()); // Сохраняем данный момент времени в sharedPreference
            myWeather.add(weather);
        }
        return myWeather;
    }

    private void saveDateNow(String dateNow) {
        SharedPreferences.Editor edit = SharedPref.getSharedPreferences().edit();
        edit.putString(Const.DATA_NOW, dateNow);
        edit.apply();
    }
}
