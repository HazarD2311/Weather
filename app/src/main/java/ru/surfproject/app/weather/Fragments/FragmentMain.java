package ru.surfproject.app.weather.Fragments;


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

import ru.surfproject.app.weather.Adapters.MainRecyclerAdapter;
import ru.surfproject.app.weather.Models.MainItemsRecyclerAdapter;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FragmentMain extends Fragment {
    RecyclerView recyclerViewMain;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().setTitle("Воронеж"); // Устанавливаем город
        recyclerViewFilling(view); // Заполнение recyclerViewMain
        return view;
    }

    private void recyclerViewFilling(View view) {
        recyclerViewMain = (RecyclerView)view.findViewById(R.id.recycler_view_main);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewMain.setLayoutManager(layoutManager); // Устанавливаем лайаут для ресайкалВью
        recyclerViewMain.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(elementsForRecyclerView()); // Создаём адаптер, элементы для него получаем в методе elementsForRecyclerView()
        recyclerViewMain.setAdapter(mainRecyclerAdapter); // Применяем адаптер для recyclerViewMain
    }

    // Тестовый метод для заполнения recyclerViewMain
    private List<MainItemsRecyclerAdapter> elementsForRecyclerView(){
        List<MainItemsRecyclerAdapter> test = new ArrayList<>();
        for (int i=0;i<15;i++){
            test.add(new MainItemsRecyclerAdapter(R.drawable.icon,"Сегодня"+i,"Снег"+i,String.valueOf(-5+i)));
        }
        return test;
    }
}
