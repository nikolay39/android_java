package com.example.android.sunshine.model.api;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

import com.example.android.sunshine.model.entity.DayForecast;
import com.example.android.sunshine.model.entity.IntervalDaysForecast;

public interface ApiService
{

    @GET
    Flowable<IntervalDaysForecast> getForecastWeather(@Url String url);
}
