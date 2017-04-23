package ru.surfproject.app.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.R;

public class ListCitiesAdapter extends RecyclerView.Adapter<ListCitiesAdapter.ViewHolder> {

    private CityOnItemClickListener cityOnItemClickListener;
    private List<String> listCities = new ArrayList<>();

    public ListCitiesAdapter(CityOnItemClickListener cityOnItemClickListener) {
        this.cityOnItemClickListener = cityOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_cities,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String city = listCities.get(position);
        holder.nameCity.setText(city);
        holder.layoutCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityOnItemClickListener != null) {
                    cityOnItemClickListener.onItemClick(view, city);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameCity;
        private LinearLayout layoutCity;

        public ViewHolder(View itemView) {
            super(itemView);
            nameCity = (TextView) itemView.findViewById(R.id.tv_name_city);
            layoutCity = (LinearLayout) itemView.findViewById(R.id.layout_city);
        }
    }

    public interface CityOnItemClickListener {
        void onItemClick(View v, String name);
    }

    public void updateCityList(List<String> listCities) {
        this.listCities.clear();
        this.listCities.addAll(listCities);
        this.notifyDataSetChanged();
    }
    public void clearRecycler() {
        this.listCities.clear();
        this.notifyDataSetChanged();
    }
}
