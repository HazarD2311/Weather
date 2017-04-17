package ru.surfproject.app.weather;

public class Const {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final String WEATHER_API = "17c37b1a89b27832b33fe590e0c7a2b2";

    // Фрагменты
    public static final String WEATHER_FRAGMENT = "WEATHER_FRAGMENT";
    public static final String FAVOURITES_FRAGMENT = "FAVOURITES_FRAGMENT";

    public static final String BASE_URL = "http://api.openweathermap.org/";
    public static final String BASE_URL_GOOGLE = "https://maps.googleapis.com/";
    public static final String KEY_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    // Значения для bundle
    public static final String BUNDLE_WEATHER = "listWeather";

    // SharedPreference
    public static final String PREFS_SHARED_DATE = "prefs_shared_date";
    public static final String DATA_NOW = "dataNow";
    public static final long TIME_FOR_UPDATE = 7200000;

    // onActivityResult
    public static final int REQUEST_CODE_NEW_CITY = 1;

    // Настройка погоды
    public static final String CNT = "10";
    public static final String UTILS = "metric";
    public static final String LANG = "ru";
    public static final String TYPE_OF_WEATHER = "pref_temperature";
    public static final String TYPE_OF_SPEED = "pref_speed";
    public static final String TYPE_OF_PRESSURE = "pref_pressure";

    //RequestCodes
    public static final int REQUEST_CODE_PERMISSION = 1000;
}
