package com.example.android.sunshine.di.modules;

import com.example.android.sunshine.model.database.room.ForecastDao;
import com.example.android.sunshine.model.repo.db.ReposDb;
import com.example.android.sunshine.model.repo.db.ReposRoom;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DatabaseModule.class, DbDao.class})
public class DbModule {

    @Provides
    @Singleton
    public ReposDb reposDb(ForecastDao forecastDao) {
        return new ReposRoom(forecastDao);
    }
}
