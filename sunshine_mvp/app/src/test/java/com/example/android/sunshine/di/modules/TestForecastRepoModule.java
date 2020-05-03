package com.example.android.sunshine.di.modules;

import com.example.android.sunshine.model.repo.ForecastRepo;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

@Module
public class TestForecastRepoModule
{
    @Provides
    public ForecastRepo forecastRepo()
    {
        return Mockito.mock(ForecastRepo.class);
    }
}
