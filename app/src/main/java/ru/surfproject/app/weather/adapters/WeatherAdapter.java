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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        holder.imageTypeWeather.setImageResource(weather.getImage());
        holder.textViewWeekday.setText(weather.getDay());
        holder.textViewTypeWeather.setText(weather.getTypeWeather());
        holder.textViewTemperature1.setText(weather.getTemperatureDay());
        holder.textViewTemperature2.setText(weather.getTemperatureNight());

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTypeWeather;
        TextView textViewWeekday;
        TextView textViewTypeWeather;
        TextView textViewTemperature1;
        TextView textViewTemperature2;

        public ViewHolder(View itemView) {
            super(itemView);
            imageTypeWeather = (ImageView) itemView.findViewById(R.id.img_type_weather);
            textViewWeekday = (TextView) itemView.findViewById(R.id.tv_weekday);
            textViewTypeWeather = (TextView) itemView.findViewById(R.id.tv_type_weather);
            textViewTemperature1 = (TextView) itemView.findViewById(R.id.tv_temperature1);
            textViewTemperature2 = (TextView) itemView.findViewById(R.id.tv_temperature2);
        }
    }
}
