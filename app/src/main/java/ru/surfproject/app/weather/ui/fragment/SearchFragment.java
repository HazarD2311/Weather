package ru.surfproject.app.weather.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.surfproject.app.weather.App;
import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.adapter.ListCitiesAdapter;
import ru.surfproject.app.weather.model.response.city.City;

/**
 * Created by pkorl on 27.11.2016.
 */

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
        searchCity.addTextChangedListener(new TextWatcher() {
            private Timer timer=new Timer();
            private final long DELAY = 500; // Задержка в миллисекундах

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, final int count) {
                progressCity.setVisibility(View.GONE);
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getCityByName(s.toString());
                                        if (count == 0) {
                                            listCitys.clear();
                                            if (listCitiesAdapter != null) {
                                                listCitiesAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                            }
                        },
                        DELAY
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void getCityByName(String text) {
        progressCity.setVisibility(View.VISIBLE);
        Call<City> call = App.getAPIServiceGoogle().getCity(text, "(cities)", getString(R.string.google_maps_key));
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                if (response.isSuccessful()) {
                    listCitys.clear();
                    for (int i = 0; i < response.body().predictions.size(); i++) {
                        listCitys.add(response.body().predictions.get(i).description);
                    }
                } else {

                }
                progressCity.setVisibility(View.GONE);
                listCitiesAdapter = new ListCitiesAdapter(cityClick, listCitys);
                recyclerCities.setAdapter(listCitiesAdapter);
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressCity.setVisibility(View.GONE);
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
