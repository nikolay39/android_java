package com.example.android.sunshine.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface MainView extends MvpView {
    void showLoading();
    void hideLoading();
    void onClick(long date);
    void init();
    void updateList();

}
