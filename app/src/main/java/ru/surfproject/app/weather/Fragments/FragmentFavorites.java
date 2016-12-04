package ru.surfproject.app.weather.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 03.12.2016.
 */

public class FragmentFavorites extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        getActivity().setTitle("Места");
        return view;
    }
}
