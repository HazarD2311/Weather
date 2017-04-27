package ru.surfproject.app.weather.model;

/**
 * Created by hazard on 17.04.17.
 */

public class Speed {

    private Double meterSec;
    private Double kmHour;
    private Double mileHour;

    public Speed(Double meterSec) {
        this.meterSec = meterSec;
        this.kmHour = intoKmHour(meterSec);
        this.mileHour = intoMileHour(meterSec);
    }

    private Double intoMileHour(Double meterSec) {
        return meterSec * 2.23;
    }

    private Double intoKmHour(Double meterSec) {
        return meterSec * 3.57;
    }

    public int getMeterSec() {
        return meterSec.intValue();
    }

    public int getKmHour() {
        return kmHour.intValue();
    }

    public int getMileHour() {
        return mileHour.intValue();
    }
}
