package com.example.android.sunshine.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.android.sunshine.model.database.room.ForecastEntry;
import com.example.android.sunshine.model.repo.ForecastRepo;
import com.example.android.sunshine.view.DetailView;


import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

@InjectViewState
public class DetailPresenter extends MvpPresenter<DetailView>
{
    @Inject public ForecastRepo forecastRepo;
    private Scheduler scheduler;

    public DetailPresenter(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    @SuppressLint("CheckResult")
    public void loadForecast(long date) {

            //getViewState().showLoading();
            forecastRepo.loadForecastByLongDate(date)
                    .observeOn(scheduler)
                    .subscribe(new Consumer<ForecastEntry>() {
                                   @Override
                                   public void accept(ForecastEntry forecastEntry) throws Exception {
                                       Timber.d("get forecast");
                                       getViewState().setDate(forecastEntry.getDateTimeMillis());
                                       getViewState().setDescription(forecastEntry.getWeatherId());

                                       getViewState().setHighTempView(forecastEntry.getHigh());
                                       getViewState().setMinTempView(forecastEntry.getLow());
                                       getViewState().setHumidity(forecastEntry.getHumidity());
                                       getViewState().setPressure(forecastEntry.getPressure());
                                       getViewState().setWeatherIcon(forecastEntry.getWeatherId());
                                       getViewState().setWindMeasurement(forecastEntry.getWindSpeed(), forecastEntry.getWindDirection());
                                       Timber.d("new settingbиди");
                                   }
                               }
                            ,
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Timber.e(throwable, "Failed to get user");
                                }
                            });

        }
}
