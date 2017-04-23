package ru.surfproject.app.weather.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.interactor.SearchInteractroImpl;
import ru.surfproject.app.weather.interfaces.search_map.SearchInteractor;
import ru.surfproject.app.weather.interfaces.search_map.SearchPresenter;
import ru.surfproject.app.weather.interfaces.search_map.SearchView;
import ru.surfproject.app.weather.model.response.city.Prediction;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


public class SearchPresenterImpl implements SearchPresenter {

    private SearchInteractor interactor;
    private SearchView view;
    private Subscription subscriptionGetCity;

    public SearchPresenterImpl(SearchView view, Context context) {
        this.view = view;
        interactor = new SearchInteractroImpl(this, context);
    }

    @Override
    public void onPause() {
        if (subscriptionGetCity != null) {
            subscriptionGetCity.unsubscribe();
        }
    }

    @Override
    public void showResultPresenter(Observable<Prediction> observableGetCity) {
        if (view != null) {
            subscriptionGetCity = observableGetCity.subscribe(new Subscriber<Prediction>() {
                List<String> listCitys = new ArrayList<>();

                @Override
                public void onCompleted() {
                    view.showProgress(false);
                    view.showResult(listCitys);
                }

                @Override
                public void onError(Throwable e) {
                    view.showProgress(false);
                    view.showError(e.getMessage());
                }

                @Override
                public void onNext(Prediction prediction) {
                    listCitys.add(prediction.description);
                }
            });
        }
    }

    @Override
    public void getCityName(String nameCity) {
        if (view != null) {
            view.showProgress(true);
            interactor.getCityNameInteractor(nameCity);
        }
    }
}
