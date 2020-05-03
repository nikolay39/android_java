package com.example.android.sunshine.model.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ForecastEntry.class}, version = 1, exportSchema = false)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract ForecastDao forecastDao();

}