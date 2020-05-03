package com.example.android.sunshine.model.repo;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.android.sunshine.App;
import com.example.android.sunshine.model.api.ApiService;
import com.example.android.sunshine.model.common.NetworkUtils;
import com.example.android.sunshine.model.common.staticWrappers.OpenWeatherConverter;
import com.example.android.sunshine.model.database.room.ForecastEntry;
import com.example.android.sunshine.model.entity.IntervalDaysForecast;
import com.example.android.sunshine.model.repo.db.ReposDb;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe;

public class ForecastRepo

{
    ReposDb roomDb;
    ApiService api;
    public ForecastRepo(ReposDb roomDb, ApiService api) {
        this.roomDb = roomDb;
        this.api = api;
    }


    public Flowable<List<ForecastEntry>> loadWeather() {
        if (NetworkUtils.isNetworkConnected()) {
            return api.getForecastWeather(NetworkUtils.getUrl().toString()).subscribeOn(Schedulers.io()).flatMap(IntervalDaysForecast -> {
                Timber.d("updateForecast count days %d ", IntervalDaysForecast.getDayForecasts().size());
                List<ForecastEntry>  forecastEntryList = OpenWeatherConverter.convertData(IntervalDaysForecast);
                roomDb.delete();
                Timber.d("deleted data");
                roomDb.insert(forecastEntryList);
                Timber.d("inserted data");
                System.out.println("size list " + forecastEntryList.size());
                return Flowable.just(forecastEntryList);
            });
        } else {
            System.out.println("load from db");
            return roomDb.select();
        }
    }
    public Flowable<ForecastEntry> loadForecastByLongDate(long date) {
        return roomDb.getForecastByLongDate(date);
    }
}
