package ru.surfproject.app.weather.model;

/**
 * Created by hazard on 17.04.17.
 */

public class Pressure {

    private Double hPa;
    private Double mmRtSt;

    public Pressure(Double hPa) {
        this.hPa = hPa;
        this.mmRtSt = intoMmRtSt(hPa);
    }

    private Double intoMmRtSt(Double hPa) {
        return hPa * 0.75;
    }

    public int gethPa() {
        return hPa.intValue();
    }

    public int getMmRtSt() {
        return mmRtSt.intValue();
    }
}
