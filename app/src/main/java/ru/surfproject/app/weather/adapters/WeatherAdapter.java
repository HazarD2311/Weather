package ru.surfproject.app.weather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.models.Weather;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 04.12.2016.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<Weather> weatherList = new ArrayList<>();

    public WeatherAdapter(List<Weather> itemsRecyclerAdapterList) {
        this.weatherList = itemsRecyclerAdapterList;
    }

    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather mainItemsRecyclerAdapter = weatherList.get(position);
        holder.imageTypeweather.setImageResource(mainItemsRecyclerAdapter.getImage());
        holder.textViewWeekday.setText(mainItemsRecyclerAdapter.getDay());
        holder.textViewTypeWeather.setText(mainItemsRecyclerAdapter.getTypeWeather());
        holder.textViewTemperature1.setText(mainItemsRecyclerAdapter.getTemperatureDay());
        holder.textViewTemperature2.setText(mainItemsRecyclerAdapter.getTemperatureNight());

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTypeweather;
        TextView textViewWeekday;
        TextView textViewTypeWeather;
        TextView textViewTemperature1;
        TextView textViewTemperature2;

        public ViewHolder(View itemView) {
            super(itemView);
            imageTypeweather = (ImageView) itemView.findViewById(R.id.img_type_weather);
            textViewWeekday = (TextView) itemView.findViewById(R.id.tv_weekday);
            textViewTypeWeather = (TextView) itemView.findViewById(R.id.tv_type_weather);
            textViewTemperature1 = (TextView) itemView.findViewById(R.id.tv_temperature1);
            textViewTemperature2 = (TextView) itemView.findViewById(R.id.tv_temperature2);
        }
    }
}
