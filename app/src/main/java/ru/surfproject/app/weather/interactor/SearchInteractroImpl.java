package ru.surfproject.app.weather.interactor;

import android.content.Context;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.interfaces.search_map.SearchInteractor;
import ru.surfproject.app.weather.interfaces.search_map.SearchPresenter;
import ru.surfproject.app.weather.model.response.city.City;
import ru.surfproject.app.weather.model.response.city.Prediction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchInteractroImpl implements SearchInteractor {

    private SearchPresenter presenter;
    private Context context;

    public SearchInteractroImpl(SearchPresenter presenter, Context context) {
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public void getCityNameInteractor(String nameCity) {
        presenter.showResultPresenter(observableGetCity(nameCity));
    }

    private Observable<Prediction> observableGetCity(String nameCity) {
        Observable<City> observable = App.getInstanceServiceGoogle().getCity(nameCity, "(cities)", context.getString(R.string.google_maps_key));
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<City, Observable<Prediction>>() {
                    @Override
                    public Observable<Prediction> call(City city) {
                        return Observable.from(city.predictions);
                    }
                });
    }
}
