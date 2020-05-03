package com.example.android.sunshine.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.android.sunshine.model.database.room.ForecastEntry;
import com.example.android.sunshine.model.repo.ForecastRepo;
import com.example.android.sunshine.view.ListRowView;
import com.example.android.sunshine.view.MainView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DisposableSubscriber;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView>
{
    private Scheduler mainThreadShceduler;
    @Inject public ForecastRepo forecastRepo;
    private ListPresenter listPresenter = new ListPresenter();
    private String url;


    class ListPresenter implements IListPresenter
    {
        List<ForecastEntry> forecastEntryList = new ArrayList<>();

        @Override
        public void bindView(ListRowView rowView)
        {
            ForecastEntry forecastEntry = forecastEntryList.get(rowView.getPos());
            rowView.setWeatherIcon(forecastEntry.getWeatherId());
            rowView.setDate(forecastEntry.getDateTimeMillis());
            rowView.setDescription(forecastEntry.getWeatherId());
            rowView.setHighTempView(forecastEntry.getHigh());
            rowView.setMinTempView(forecastEntry.getLow());
        }

        @Override
        public int getItemCount() {
            return forecastEntryList.size();
        }

        @Override
        public long getLongDate(int position) {
            return forecastEntryList.get(position).getDateTimeMillis();
        }


    }

    public MainPresenter(Scheduler mainThreadShceduler)
    {

        this.mainThreadShceduler = mainThreadShceduler;
    }

    @Override
    protected void onFirstViewAttach()
    {
        super.onFirstViewAttach();
        getViewState().init();
        loadData();


    }

    @SuppressLint("CheckResult")
    public void loadData(){
        //Log.d("mainPresenter", "load Data");
        getViewState().showLoading();
        Disposable d = forecastRepo.loadWeather()
                .observeOn(mainThreadShceduler)
                .subscribeWith(new DisposableSubscriber<List<ForecastEntry>>() {
                    @Override
                    public void onStart() {
                        request(1);
                        System.out.println("Start Done!");
                    }
                    @Override
                    public void onNext(List<ForecastEntry> forecastEntryList) {
                        listPresenter.forecastEntryList = forecastEntryList;
                        getViewState().hideLoading();
                        getViewState().updateList();
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete Done!");
                    }
                    //Log.d("mainPresenter", "accept_MainPresenter");
                    //Timber.d("get forecastEntryList size %d", forecastEntryList.size());
                });


    }

    public ListPresenter getListPresenter()
    {
        return listPresenter;
    }
}
