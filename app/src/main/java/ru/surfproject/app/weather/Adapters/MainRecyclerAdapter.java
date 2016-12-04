package ru.surfproject.app.weather.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.Models.MainItemsRecyclerAdapter;
import ru.surfproject.app.weather.R;

/**
 * Created by pkorl on 04.12.2016.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private List<MainItemsRecyclerAdapter> itemsRecyclerAdapterList = new ArrayList<>();

    public MainRecyclerAdapter(List<MainItemsRecyclerAdapter> itemsRecyclerAdapterList) {
        this.itemsRecyclerAdapterList = itemsRecyclerAdapterList;
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, int position) {
        MainItemsRecyclerAdapter mainItemsRecyclerAdapter = itemsRecyclerAdapterList.get(position);
        holder.imageTypeweather.setImageResource(mainItemsRecyclerAdapter.getImage());
        holder.textViewWeekday.setText(mainItemsRecyclerAdapter.getDay());
        holder.textViewTypeWeather.setText(mainItemsRecyclerAdapter.getTypeWeather());
        holder.textViewTemperature.setText(mainItemsRecyclerAdapter.getTemperature());

    }

    @Override
    public int getItemCount() {
        return itemsRecyclerAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTypeweather;
        TextView textViewWeekday;
        TextView textViewTypeWeather;
        TextView textViewTemperature;
        public ViewHolder(View itemView) {
            super(itemView);
            imageTypeweather = (ImageView) itemView.findViewById(R.id.img_type_weather);
            textViewWeekday = (TextView) itemView.findViewById(R.id.tv_weekday);
            textViewTypeWeather = (TextView) itemView.findViewById(R.id.tv_type_weather);
            textViewTemperature = (TextView) itemView.findViewById(R.id.tv_temperature);
        }
    }
}
