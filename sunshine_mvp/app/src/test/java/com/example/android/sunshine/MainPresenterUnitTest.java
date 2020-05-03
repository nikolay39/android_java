package com.example.android.sunshine;



import com.example.android.sunshine.di.DaggerTestComponent;
import com.example.android.sunshine.di.TestComponent;
import com.example.android.sunshine.di.modules.TestForecastRepoModule;
import com.example.android.sunshine.model.database.room.ForecastEntry;
import com.example.android.sunshine.model.repo.ForecastRepo;
import com.example.android.sunshine.presenter.MainPresenter;
import com.example.android.sunshine.view.MainView;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.times;

public class MainPresenterUnitTest
{
    private MainPresenter presenter;
    private TestScheduler testScheduler;

    @Mock MainView mainView;

    @BeforeClass
    public static void setupClass()
    {

        System.out.println("Start Class");
    }

    @AfterClass
    public static void tearDownClass()
    {
        System.out.println("After Class");
    }

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        testScheduler = new TestScheduler();
        presenter = Mockito.spy(new MainPresenter(testScheduler));
    }

    @After
    public void tearDown()
    {
        System.out.println("Ater Test");
    }

    @Test
    public void onFirstViewAttach()
    {
        presenter.attachView(mainView);
        Mockito.verify(mainView).init();
    }

    @Test
    public void loadInfoSuccess()
    {
        //

        TestComponent component = DaggerTestComponent.builder()
                .testForecastRepoModule(new TestForecastRepoModule(){
                    @Override
                    public ForecastRepo forecastRepo()
                    {
                        ForecastRepo repo = super.forecastRepo();
                        Mockito.when(repo.loadWeather()).thenReturn(Flowable.just(new ArrayList<ForecastEntry>()));

                        return repo;
                    }
                }).build();
        component.inject(presenter);
        presenter.attachView(mainView);
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);

        Mockito.verify(mainView).init();
        Mockito.verify(mainView, times(1)).hideLoading();
        Mockito.verify(mainView, times(1)).showLoading();
    }
    @Test
    public void loadInfoFail(){
        //
        String error ="my error";
        TestComponent component = DaggerTestComponent.builder()
                .testForecastRepoModule(new TestForecastRepoModule(){
                    @Override
                    public ForecastRepo forecastRepo()
                    {
                        ForecastRepo repo = super.forecastRepo();
                        Mockito.when(repo.loadWeather()).thenReturn(Flowable.error(new RuntimeException(error)));
                        return repo;
                    }
                }).build();
        component.inject(presenter);
        presenter.attachView(mainView);
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
        Mockito.verify(mainView).init();
        Mockito.verify(mainView, times(1)).hideLoading();
        Mockito.verify(mainView, times(1)).showLoading();

    }



}
