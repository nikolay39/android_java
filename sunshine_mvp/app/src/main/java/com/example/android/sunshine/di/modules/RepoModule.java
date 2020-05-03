package com.example.android.sunshine.di.modules;

import com.example.android.sunshine.model.api.ApiService;
import com.example.android.sunshine.model.repo.ForecastRepo;
import com.example.android.sunshine.model.repo.db.ReposDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DbModule.class, ApiModule.class})
public class RepoModule {
    @Singleton
    @Provides
    public ForecastRepo forecastRepo(ReposDb db, ApiService api) {
        return new ForecastRepo(db, api);
    }

}
