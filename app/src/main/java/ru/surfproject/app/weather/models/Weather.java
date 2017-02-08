package ru.surfproject.app.weather.models;

/**
 * Created by pkorl on 04.12.2016.
 */

public class Weather {
    private int image;
    private String day;
    private String typeWeather;
    private String temperatureDay;
    private String temperatureNight;

    public Weather() {

    }

    public Weather(int image, String day, String typeWeather, String temperature1, String temperature2) {
        this.image = image;
        this.day = day;
        this.typeWeather = typeWeather;
        this.temperatureDay = temperature1;
        this.temperatureNight = temperature2;
    }

    public void setDay(int dayOfTheWeek, int day, int month) {
        this.day = (parseDayOfTheWeek(dayOfTheWeek) + ", " + String.valueOf(day) + " " + parseMonth(month));
    }

    private String parseMonth(int month) {
        switch (month) {
            case 0:
                return "января";
            case 1:
                return "февраля";
            case 2:
                return "марта";
            case 3:
                return "апреля";
            case 4:
                return "май";
            case 5:
                return "июня";
            case 6:
                return "июля";
            case 7:
                return "августа";
            case 8:
                return "сентября";
            case 9:
                return "октября";
            case 10:
                return "ноября";
            case 11:
                return "декабря";
            default:
                return null;
        }
    }

    private String parseDayOfTheWeek(int day) {
        switch (day) {
            case 0:
                return "Вс";
//                return getString(R.string.sunday);
            case 1:
                return "Пн";
            case 2:
                return "Вт";
            case 3:
                return "Ср";
            case 4:
                return "Чт";
            case 5:
                return "Пт";
            case 6:
                return "Сб";

            default:
                return null;
        }
    }

    public int getImage() {
        return image;
    }

    public String getDay() {
        return day;
    }

    public String getTypeWeather() {
        return typeWeather;
    }

    public String getTemperatureDay() {
        return temperatureDay;
    }

    public String getTemperatureNight() {
        return temperatureNight;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTypeWeather(String typeWeather) {
        this.typeWeather = typeWeather;
    }

    public void setTemperatureDay(String temperatureDay) {
        this.temperatureDay = temperatureDay;
    }

    public void setTemperatureNight(String temperatureNight) {
        this.temperatureNight = temperatureNight;
    }
}
