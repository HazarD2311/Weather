package ru.surfproject.app.weather.model;

/**
 * класс для перевода температуры в другие форматы
 */

public class Temperature {

    private Double celsius;
    private Double fahrenheit;

    public Temperature(Double celsius) {
        this.celsius = celsius;
        this.fahrenheit = fromCelsiusToFahrenheit(celsius);
    }

    private Double fromCelsiusToFahrenheit(Double celsius) {
        return (Double) ((celsius * 1.8) + 32);
    }


    public int getIntCelsius() {
        return celsius.intValue();
    }

    public int getIntFahrenheit() {
        return fahrenheit.intValue();
    }

    public Double getCelsius() {
        return celsius;
    }

    public Double getFahrenheit() {
        return fahrenheit;
    }

    public void setCelsius(Double celsius) {
        this.celsius = celsius;
    }

    public void setFahrenheit(Double fahrenheit) {
        this.fahrenheit = fahrenheit;
    }
}
