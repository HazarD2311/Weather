package ru.surfproject.app.weather.util;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.surfproject.app.weather.Const;

public class TimeUtils {
    public static String getDatetimeNow() {
        Calendar c = Calendar.getInstance();
        CharSequence s = DateFormat.format(Const.KEY_DATE_TIME, c.getTime());
        return s.toString();
    }

    public static boolean isNecessaryUpdateWeather(String strDataNow, String strDataPref) {
        boolean flag = false;
        if (!strDataPref.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat(Const.KEY_DATE_TIME);
            try {
                Date date1 = format.parse(strDataNow);
                Date date2 = format.parse(strDataPref);
                long diff = date1.getTime() - date2.getTime();
                Log.d("TEST",strDataNow + "\n"+ strDataPref + "\n" +diff);
                if (diff > Const.TIME_FOR_UPDATE) {
                    Log.d("TEST", "прошло больше 2-х часов, надо обновлять данные");
                    flag = true;
                } else {
                    Log.d("TEST", "прошло меньше 2-х часов, НЕ надо обновлять данные");
                    flag = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("TEST", "первый раз запускаем, надо к серваку обращаться");
            flag = true;
        }
       return flag;
    }
}
