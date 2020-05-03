package com.example.android.sunshine.di;


import com.example.android.sunshine.ForecastRepoInstrumentedTest;
import com.example.android.sunshine.di.modules.AppModule;
import com.example.android.sunshine.di.modules.RepoModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, RepoModule.class})
public interface TestComponent
{
    void inject(ForecastRepoInstrumentedTest test);
}
