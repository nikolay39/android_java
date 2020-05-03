package com.example.android.sunshine.model.database.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

@Dao
public interface ForecastDao {
    @Query("SELECT * FROM forecast ORDER by dateTimeMillis")
    public Flowable<List<ForecastEntry>> loadForecastData();

    @Insert
    public void insertForecastData(Iterable<ForecastEntry> listForecastEntry);

    //@Update(onConflict = OnConflictStrategy.REPLACE)
    //void updateTask(ForecastEntry forecastEntry);

    @Query("DELETE from forecast")
    public void deleteForecastData();

    @Query("SELECT * FROM forecast WHERE dateTimeMillis = :date")
    public Flowable<ForecastEntry> getForecastLongByDate(long date);

}
