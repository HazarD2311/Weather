package ru.surfproject.app.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.surfproject.app.weather.model.Weather;
import ru.surfproject.app.weather.R;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private List<Weather> weatherList = new ArrayList<>();
    private Context context;
    Animation animationFadeIn;
    Animation animationFadeOut;

    public WeatherAdapter(List<Weather> itemsRecyclerAdapterList, Context context) {
        this.weatherList = itemsRecyclerAdapterList;
        this.context = context;
        animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(context, R.anim.fadeout);
    }

    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Weather weather = weatherList.get(position);
        holder.imageTypeWeather.setImageResource(weather.getImage());
        holder.textViewWeekday.setText(weather.getDay());
        holder.textViewTypeWeather.setText(weather.getTypeWeather());
        holder.textViewTemperature1.setText(weather.getTemperatureDay());
        holder.textViewTemperature2.setText(weather.getTemperatureNight());
        holder.textViewHumidity.setText(weather.getHumidity());
        holder.textViewtPressure.setText(weather.getPressure());
        holder.textViewWindSpeed.setText(weather.getWindSpeed());
        holder.textViewWindDirection.setText(weather.getWindDirection());

        holder.layoutWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.linerLayoutAdditionallyInfo.getVisibility() == View.GONE) {
                    holder.linerLayoutAdditionallyInfo.setVisibility(View.VISIBLE);
                    holder.linerLayoutAdditionallyInfo.startAnimation(animationFadeIn);
                } else {
                    holder.linerLayoutAdditionallyInfo.startAnimation(animationFadeOut);
                    holder.linerLayoutAdditionallyInfo.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutWeather;
        LinearLayout linerLayoutAdditionallyInfo;
        ImageView imageTypeWeather;
        TextView textViewWeekday;
        TextView textViewTypeWeather;
        TextView textViewTemperature1;
        TextView textViewTemperature2;
        TextView textViewHumidity;
        TextView textViewtPressure;
        TextView textViewWindSpeed;
        TextView textViewWindDirection;

        public ViewHolder(View itemView) {
            super(itemView);
            linerLayoutAdditionallyInfo = (LinearLayout) itemView.findViewById(R.id.layout_additionally_info);
            layoutWeather = (LinearLayout) itemView.findViewById(R.id.layout_weather);
            imageTypeWeather = (ImageView) itemView.findViewById(R.id.img_type_weather);
            textViewWeekday = (TextView) itemView.findViewById(R.id.tv_weekday);
            textViewTypeWeather = (TextView) itemView.findViewById(R.id.tv_type_weather);
            textViewTemperature1 = (TextView) itemView.findViewById(R.id.tv_temperature1);
            textViewTemperature2 = (TextView) itemView.findViewById(R.id.tv_temperature2);
            textViewHumidity = (TextView) itemView.findViewById(R.id.tv_humidity);
            textViewtPressure = (TextView) itemView.findViewById(R.id.tv_pressure);
            textViewWindSpeed = (TextView) itemView.findViewById(R.id.tv_wind_speed);
            textViewWindDirection = (TextView) itemView.findViewById(R.id.tv_wind_direction);
        }
    }

    public void updateWeatherList(List<Weather> listWeather) {
        weatherList.clear();
        weatherList.addAll(listWeather);
    }
}
