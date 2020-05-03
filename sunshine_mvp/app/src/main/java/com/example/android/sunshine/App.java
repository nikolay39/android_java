package com.example.android.sunshine;

import android.app.Application;
import android.content.Context;

import com.example.android.sunshine.di.AppComponent;
import com.example.android.sunshine.di.DaggerAppComponent;
import com.example.android.sunshine.di.modules.AppModule;
import com.example.android.sunshine.presenter.MainPresenter;
import com.example.android.sunshine.view.MainActivity;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

import dagger.Provides;
import timber.log.Timber;

public class App extends Application
{
    private static App instance;
    private static Context context;

    private AppComponent appComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        Stetho.initializeWithDefaults(this);

        Timber.plant(new Timber.DebugTree());
        App.context = getApplicationContext();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

   }

    public static App getInstance(){
        return instance;
    }

    public static Context getAppContext() {
        return App.context;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
