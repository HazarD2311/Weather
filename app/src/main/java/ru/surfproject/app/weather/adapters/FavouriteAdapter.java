package ru.surfproject.app.weather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.models.Favourite;
import ru.surfproject.app.weather.R;

/**
 * Created by ПК on 12/5/2016.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private List<Favourite> itemsFavouriteRecycleAdapterList = new ArrayList<>();

    public FavouriteAdapter(List<Favourite> itemsFavouriteRecycleAdapterList) {
        this.itemsFavouriteRecycleAdapterList = itemsFavouriteRecycleAdapterList;
    }

    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.ViewHolder holder, int position) {
        Favourite favouriteItemsRecyclerAdapter = itemsFavouriteRecycleAdapterList.get(position);
        holder.textViewCity.setText(favouriteItemsRecyclerAdapter.getCity());
        holder.textViewAverageTemperature.setText(favouriteItemsRecyclerAdapter.getAverageTemperature());
    }

    @Override
    public int getItemCount() {
        return itemsFavouriteRecycleAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCity;
        public TextView textViewAverageTemperature;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewCity = (TextView) itemView.findViewById(R.id.favourite_city);
            textViewAverageTemperature = (TextView) itemView.findViewById(R.id.favourite_temperature);
        }

    }
}
