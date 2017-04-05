package ru.surfproject.app.weather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapter.ListCitiesAdapter;
import ru.surfproject.app.weather.model.response.city.City;
import ru.surfproject.app.weather.model.response.city.Prediction;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private EditText searchCity;
    private List<String> listCitys = new ArrayList<>();
    private RecyclerView recyclerCities;
    private ListCitiesAdapter listCitiesAdapter;
    private ProgressBar progressCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressCity = (ProgressBar) view.findViewById(R.id.progress_city);
        recyclerCities = (RecyclerView) view.findViewById(R.id.recycler_cities);
        recyclerCities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCities.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        searchCity = (EditText) view.findViewById(R.id.edt_search_city);

        Observable<CharSequence> observable = RxTextView.textChanges(searchCity);
        observable.skip(1)
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(300, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        final boolean empty = TextUtils.isEmpty(charSequence);
                        if (empty) {
                            listCitys.clear();
                            listCitiesAdapter.notifyDataSetChanged();
                        }
                        return !empty;
                    }
                })
                .subscribe(new Subscriber<CharSequence>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.getMessage();
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        progressCity.setVisibility(View.GONE);
                        getCityByName(charSequence.toString());
                    }
                });
        return view;
    }

    private void getCityByName(String text) {
        progressCity.setVisibility(View.VISIBLE);
        Observable<City> observable = App.getAPIServiceGoogle().getCity(text, "(cities)", getString(R.string.google_maps_key));
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<City, Observable<Prediction>>() {
                    @Override
                    public Observable<Prediction> call(City city) {
                        return Observable.from(city.predictions);
                    }
                }).subscribe(new Subscriber<Prediction>() {
            @Override
            public void onCompleted() {
                progressCity.setVisibility(View.GONE);
                listCitiesAdapter = new ListCitiesAdapter(cityClick, listCitys);
                recyclerCities.setAdapter(listCitiesAdapter);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "Ошибка\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressCity.setVisibility(View.GONE);
            }

            @Override
            public void onNext(Prediction prediction) {
                listCitys.add(prediction.description);
            }
        });
    }

    private ListCitiesAdapter.CityOnItemClickListener cityClick = new ListCitiesAdapter.CityOnItemClickListener() {
        @Override
        public void onItemClick(View v, String name) {
            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
        }
    };
}
