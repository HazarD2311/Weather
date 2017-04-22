package ru.surfproject.app.weather.presenter;

import android.content.Context;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.Const;
import ru.surfproject.app.weather.SharedPref;
import ru.surfproject.app.weather.db.dao.WeatherDao;
import ru.surfproject.app.weather.interactor.WeatherInteractorImpl;
import ru.surfproject.app.weather.interfaces.weather.WeatherInteractor;
import ru.surfproject.app.weather.interfaces.weather.WeatherPresenter;
import ru.surfproject.app.weather.interfaces.weather.WeatherView;
import ru.surfproject.app.weather.model.Weather;
import ru.surfproject.app.weather.model.response.WeatherWeek;
import ru.surfproject.app.weather.util.TimeUtils;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pkorl on 19.04.2017.
 */

public class WeatherPresenterImpl implements WeatherPresenter {
    private WeatherInteractor interactor;
    private WeatherView view;
    private CompositeSubscription mCompositeSubscription  = new CompositeSubscription();

    public WeatherPresenterImpl(WeatherView view, Context context) {
        this.view = view;
        interactor = new WeatherInteractorImpl(this, context);
    }

    @Override
    public void showErrorPresenter(String error) {
        if (view != null) {
            view.showError(error);
        }
    }

    @Override
    public void showResultPresenter(Observable<List<Weather>> weatherList) {
        if (view != null) {
            mCompositeSubscription.add(weatherList.subscribe(new Subscriber<List<Weather>>() {
                @Override
                public void onCompleted() {
                    view.showProgress(false);
                }

                @Override
                public void onError(Throwable e) {
                    view.showProgress(false);
                }

                @Override
                public void onNext(List<Weather> weathers) {
                    view.showResult(weathers);
                }
            }));
        }
    }

    @Override
    public void getWeather(final String lat, final String lon) {
        if (view != null) {
            view.showProgress(true);

            WeatherDao weatherDao;
            try {
                weatherDao = App.getHelper().getWeatherDao();
                if (weatherDao != null) {
                    mCompositeSubscription.add(weatherDao.getAllWeather()
                            .subscribe(new Subscriber<List<Weather>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                   // Toast.makeText(getActivity(), "Ошибка\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(List<Weather> weathers) {
                                    //показываем полученные данные
                                    String dataNow = TimeUtils.getDatetimeNow();
                                    String dataShared = getDateFromShared();
                                    boolean isNecessaryUpdateWeather = TimeUtils.isNecessaryUpdateWeather(dataNow, dataShared);
                                    if (isNecessaryUpdateWeather || weathers.size() == 0) {
                                        // Необходимо обновить данные, так как прошло больше 2-х часов или БД пустая
                                        view.showProgress(true);
                                        interactor.getWeatherInteractor(lat, lon, Const.CNT, Const.UTILS, Const.LANG, Const.WEATHER_API);
                                    } else {
                                        view.showProgress(false);
                                        view.showResult(weathers);
                                        //mainRecyclerAdapter.updateWeatherList(weathers);
                                        //progressBar.setVisibility(View.GONE);
                                        //placeHolderNetwork.setVisibility(View.GONE);
                                    }
                                }
                            }));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void getWeatherCoordServer(String lat, String lon) {
        if (view != null) {
            view.showProgress(true);
            interactor.getWeatherInteractor(lat, lon, Const.CNT, Const.UTILS, Const.LANG, Const.WEATHER_API);
        }
    }

    @Override
    public void onPause() {
        mCompositeSubscription.unsubscribe();
    }

    private String getDateFromShared() {
        return SharedPref.getSharedPreferences().getString(Const.DATA_NOW, "");
    }
}
