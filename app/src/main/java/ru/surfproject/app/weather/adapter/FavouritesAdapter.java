package ru.surfproject.app.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.model.Favourite;
import ru.surfproject.app.weather.R;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<Favourite> favouritesList = new ArrayList<>();

    public FavouritesAdapter(List<Favourite> itemsFavouriteRecycleAdapterList,
                             OnItemClickListener onItemClickListener,
                             OnItemLongClickListener onItemLongClickListener) {
        this.favouritesList = itemsFavouriteRecycleAdapterList;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavouritesAdapter.ViewHolder holder, final int position) {
        final Favourite favouriteItemsRecyclerAdapter = favouritesList.get(position);
        holder.textViewCity.setText(favouriteItemsRecyclerAdapter.getCity());
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(view, favouriteItemsRecyclerAdapter);
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClick(view, favouriteItemsRecyclerAdapter, holder.getAdapterPosition());
                    return true;
                }
            });
        }

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

    public void renameFavourite(int position, String newName) {
        favouritesList.get(position).setCity(newName);
        this.notifyItemChanged(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Favourite favourite);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, Favourite favourite, int position);
    }

}
