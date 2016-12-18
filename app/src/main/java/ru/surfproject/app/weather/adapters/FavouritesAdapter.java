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

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {
    private List<Favourite> favouritesList = new ArrayList<>();

    public FavouritesAdapter(List<Favourite> itemsFavouriteRecycleAdapterList) {
        this.favouritesList = itemsFavouriteRecycleAdapterList;
    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, int position) {
        Favourite favouriteItemsRecyclerAdapter = favouritesList.get(position);
        holder.textViewCity.setText(favouriteItemsRecyclerAdapter.getCity());
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCity;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewCity = (TextView) itemView.findViewById(R.id.favourite_city);
        }

    }
}
