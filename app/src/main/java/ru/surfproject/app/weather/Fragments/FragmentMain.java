package ru.surfproject.app.weather.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.adapters.WeatherAdapter;
import ru.surfproject.app.weather.models.Weather;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FragmentMain extends Fragment {
    RecyclerView recyclerViewWeather;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().setTitle("Воронеж"); // Устанавливаем город
        setupRecycler(view); // Заполнение recyclerViewWeather
        return view;
    }

    private void setupRecycler(View view) {
        recyclerViewWeather = (RecyclerView) view.findViewById(R.id.recycler_view_main);
        recyclerViewWeather.setLayoutManager(new LinearLayoutManager(getContext())); // Устанавливаем лайаут для ресайкалВью
        recyclerViewWeather.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        WeatherAdapter mainRecyclerAdapter = new WeatherAdapter(valuesForRecycler()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewWeather.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewWeather
    }

    // Тестовый метод для заполнения recyclerViewWeather
    private List<Weather> valuesForRecycler() {
        List<Weather> test = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            test.add(new Weather(R.drawable.icon, "Сегодня" + i, "Снег" + i, String.valueOf(-5 + i)));
        }
        return test;
    }
}
