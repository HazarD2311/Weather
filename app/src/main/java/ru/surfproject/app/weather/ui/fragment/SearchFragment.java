package ru.surfproject.app.weather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapter.ListCitiesAdapter;
import ru.surfproject.app.weather.interfaces.search_map.SearchPresenter;
import ru.surfproject.app.weather.interfaces.search_map.SearchView;
import ru.surfproject.app.weather.model.response.city.City;
import ru.surfproject.app.weather.model.response.city.Prediction;
import ru.surfproject.app.weather.presenter.SearchPresenterImpl;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchFragment extends Fragment implements SearchView {

    private EditText searchCity;
    private RecyclerView recyclerCities;
    private ListCitiesAdapter listCitiesAdapter;
    private ProgressBar progressCity;
    private SearchPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressCity = (ProgressBar) view.findViewById(R.id.progress_city);
        recyclerCities = (RecyclerView) view.findViewById(R.id.recycler_cities);
        recyclerCities.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCities.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        listCitiesAdapter = new ListCitiesAdapter(cityClick);
        recyclerCities.setAdapter(listCitiesAdapter);
        presenter = new SearchPresenterImpl(this, getContext());
        searchCity = (EditText) view.findViewById(R.id.edt_search_city);
        observableTextChange().subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                presenter.getCityName(charSequence.toString());
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void showProgress(boolean flag) {
        if (flag) {
            progressCity.setVisibility(View.VISIBLE);
        } else {
            progressCity.setVisibility(View.GONE);
        }
    }

    @Override
    public void showResult(List<String> listCitys) {
        listCitiesAdapter.updateCityList(listCitys);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), "Ошибка\n" + error, Toast.LENGTH_SHORT).show();
    }

    private Observable<CharSequence> observableTextChange() {
        return RxTextView.textChanges(searchCity).skip(1) // Пропускаем первый вызов
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(300, TimeUnit.MILLISECONDS) // Задержка
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        final boolean empty = TextUtils.isEmpty(charSequence);
                        return !empty;
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
