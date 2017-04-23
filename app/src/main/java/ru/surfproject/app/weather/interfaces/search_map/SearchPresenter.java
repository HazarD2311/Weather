package ru.surfproject.app.weather.interfaces.search_map;

import ru.surfproject.app.weather.model.response.city.Prediction;
import rx.Observable;

public interface SearchPresenter {

    void onPause();
    void showResultPresenter(Observable<Prediction> observableGetCity);
    void getCityName(String nameCity);
}
