package ru.surfproject.app.weather.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 27.11.2016.
 */

public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear(); // Очищаем карду от маркеров
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); // Наводим камеру на место клика
        mMap.addMarker(new MarkerOptions() // Добавляем маркер
                .position(latLng)
                .title(latLng.latitude+", "+latLng.longitude));
    }
}
