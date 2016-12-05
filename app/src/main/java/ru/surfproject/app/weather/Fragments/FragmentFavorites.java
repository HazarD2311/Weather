package ru.surfproject.app.weather.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.Adapters.FavouriteRecyclerAdapter;
import ru.surfproject.app.weather.Models.FavouriteItemsRecyclerAdapter;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FragmentFavorites extends Fragment {
    private RecyclerView favouriteRecycleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        getActivity().setTitle("Места");
        recycleViewFilling(view); //заполнение RecycleView
        return view;
    }

    private void recycleViewFilling(View view) {
        favouriteRecycleView = (RecyclerView) view.findViewById(R.id.recycler_view_favourite);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        favouriteRecycleView.setLayoutManager(layoutManager);
        favouriteRecycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        favouriteRecycleView.setItemAnimator(new DefaultItemAnimator());

        FavouriteRecyclerAdapter favouriteRecyclerAdapter = new FavouriteRecyclerAdapter(listForRecycleView());
        favouriteRecycleView.setAdapter(favouriteRecyclerAdapter);
    }

    //для теста
    private List<FavouriteItemsRecyclerAdapter> listForRecycleView() {
        List<FavouriteItemsRecyclerAdapter> testList = new ArrayList<>();

        testList.add(new FavouriteItemsRecyclerAdapter("Воронеж", "-10"));
        testList.add(new FavouriteItemsRecyclerAdapter("Нью-йорк", "+20"));
        testList.add(new FavouriteItemsRecyclerAdapter("Лондон", "+7"));

        return testList;
    }
}
