package com.example.android.sunshine.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class IntervalDaysForecast {
    private City city;
    private float message;

    @SerializedName("list")
    private List<DayForecast> dayForecasts = new ArrayList<>();

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public float getMessage() {
        return message;
    }

    public void setMessage(float message) {
        this.message = message;
    }

    public List<DayForecast> getDayForecasts() {
        return dayForecasts;
    }

    public void setDayForecasts(List<DayForecast> dayForecasts) {
        this.dayForecasts = dayForecasts;
    }





}
