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

import ru.surfproject.app.weather.adapters.FavouritesAdapter;
import ru.surfproject.app.weather.models.Favourite;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerViewFavourites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        getActivity().setTitle("Места");
        setupRecycler(view); //заполнение RecycleView
        return view;
    }

    private void setupRecycler(View view) {
        recyclerViewFavourites = (RecyclerView) view.findViewById(R.id.recycler_view_favourite);
        recyclerViewFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavourites.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL)); // Добавляем разделитель между элементами
        FavouritesAdapter favouriteRecyclerAdapter = new FavouritesAdapter(valuesForRecycle());
        recyclerViewFavourites.setAdapter(favouriteRecyclerAdapter);
    }

    //для теста
    private List<Favourite> valuesForRecycle() {
        List<Favourite> testList = new ArrayList<>();
        testList.add(new Favourite("Воронеж"));
        testList.add(new Favourite("Нью-йорк"));
        testList.add(new Favourite("Лондон"));
        return testList;
    }
}
