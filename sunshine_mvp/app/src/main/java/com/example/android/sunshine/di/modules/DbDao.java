package com.example.android.sunshine.di.modules;

import android.arch.persistence.room.RoomDatabase;

import com.example.android.sunshine.model.database.room.DatabaseManager;
import com.example.android.sunshine.model.database.room.ForecastDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
@Module
public class DbDao {
    @Provides
    @Singleton
    public ForecastDao provideDao(DatabaseManager database) {
        return database.forecastDao();
    }
}
