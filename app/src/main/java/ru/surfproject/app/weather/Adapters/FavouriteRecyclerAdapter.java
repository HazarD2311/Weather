package ru.surfproject.app.weather.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.Models.FavouriteItemsRecyclerAdapter;
import ru.surfproject.app.weather.R;

/**
 * Created by ПК on 12/5/2016.
 */

public class FavouriteRecyclerAdapter extends RecyclerView.Adapter<FavouriteRecyclerAdapter.ViewHolder>{
    private List<FavouriteItemsRecyclerAdapter> itemsFavouriteRecycleAdapterList = new ArrayList<>();

    public FavouriteRecyclerAdapter(List<FavouriteItemsRecyclerAdapter> itemsFavouriteRecycleAdapterList) {
        this.itemsFavouriteRecycleAdapterList = itemsFavouriteRecycleAdapterList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewCity;
        public TextView textViewAverageTemperature;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewCity = (TextView) itemView.findViewById(R.id.favourite_city);
            textViewAverageTemperature = (TextView) itemView.findViewById(R.id.favourite_temperature);
        }

    }

    @Override
    public FavouriteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteRecyclerAdapter.ViewHolder holder, int position) {
        FavouriteItemsRecyclerAdapter favouriteItemsRecyclerAdapter = itemsFavouriteRecycleAdapterList.get(position);
        holder.textViewCity.setText(favouriteItemsRecyclerAdapter.getCity());
        holder.textViewAverageTemperature.setText(favouriteItemsRecyclerAdapter.getAverageTemperature());
    }
    @Override
    public int getItemCount() {
        return itemsFavouriteRecycleAdapterList.size();
    }
}
