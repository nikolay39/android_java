package com.example.android.sunshine.di;

import com.example.android.sunshine.di.modules.AppModule;
import com.example.android.sunshine.di.modules.RepoModule;
import com.example.android.sunshine.model.common.NetworkUtils;
import com.example.android.sunshine.presenter.DetailPresenter;
import com.example.android.sunshine.presenter.MainPresenter;
import com.example.android.sunshine.view.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RepoModule.class})
public interface AppComponent {
    //void inject(MainActivity activity);
    void inject(MainPresenter activity);
    void inject(DetailPresenter activity);
}
