package com.example.android.sunshine.model.database.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "forecast")
public class ForecastEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long dateTimeMillis;
    private double pressure;
    private double humidity;
    private double windSpeed;
    private double windDirection;
    private double high;
    private double low;
    private int weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public ForecastEntry(
                         long dateTimeMillis,
                         double pressure,
                         double humidity,
                         double windSpeed,
                         double windDirection,
                         double high,
                         double low,
                         int weatherId) {
        this.dateTimeMillis = dateTimeMillis;
        this.pressure = pressure;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.high = high;
        this.low = low;
        this.weatherId = weatherId;
    }

}
