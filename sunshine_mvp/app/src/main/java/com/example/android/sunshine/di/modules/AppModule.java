package com.example.android.sunshine.di.modules;

import android.content.Context;

import com.example.android.sunshine.App;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private App app;

    public AppModule(App app) {
        this.app = app;
    }
    @Singleton
    @Provides
    public App app() {
        return app;
    }


    @Singleton
    @Provides
    public Context getAppContext() {
        return App.getAppContext();
    }
}
