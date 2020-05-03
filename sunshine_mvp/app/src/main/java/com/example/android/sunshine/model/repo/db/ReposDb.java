package com.example.android.sunshine.model.repo.db;

import com.example.android.sunshine.model.database.room.ForecastEntry;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface ReposDb {
        Flowable<List<ForecastEntry>> select();
        void insert(Iterable<ForecastEntry> listForecastEntry);
        void delete();
        Flowable<ForecastEntry> getForecastByLongDate(long date);
}
