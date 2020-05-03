package com.example.android.sunshine.di;

import com.example.android.sunshine.di.modules.TestForecastRepoModule;
import com.example.android.sunshine.presenter.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {TestForecastRepoModule.class})
public interface TestComponent
{
    void inject(MainPresenter presenter);
}
