package com.example.android.sunshine.model.repo.db;

import com.example.android.sunshine.model.database.room.ForecastDao;
import com.example.android.sunshine.model.database.room.ForecastEntry;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class ReposRoom implements ReposDb{

    ForecastDao forecastDao;

    public ReposRoom(ForecastDao forecastDao) {
        this.forecastDao = forecastDao;
    }

    @Override
    public Flowable<List<ForecastEntry>> select() {
        return  forecastDao.loadForecastData();
    }

    @Override
    public void insert(Iterable<ForecastEntry> listForecastEntry) {
        forecastDao.insertForecastData(listForecastEntry);

    }

    @Override
    public void delete() {
        forecastDao.deleteForecastData();

    }
    @Override
    public Flowable<ForecastEntry> getForecastByLongDate(long date) {
        return forecastDao.getForecastLongByDate(date);
    }
}
