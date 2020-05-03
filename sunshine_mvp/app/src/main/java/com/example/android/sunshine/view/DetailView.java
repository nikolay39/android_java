package com.example.android.sunshine.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface DetailView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setWeatherIcon(int weatherId);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setDate(long dateInMillis);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setDescription(int weatherId);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setHighTempView(double highInCelsius);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setMinTempView(double lowInCelsius);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setHumidity(double humidity);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setWindMeasurement(double windSpeed, double windDirection);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setPressure(double pressure);

}
