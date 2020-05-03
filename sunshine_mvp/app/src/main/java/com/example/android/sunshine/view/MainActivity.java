/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.android.sunshine.App;
import com.example.android.sunshine.R;
import com.example.android.sunshine.model.preference.SunshinePreferences;
import com.example.android.sunshine.model.common.NetworkUtils;
import com.example.android.sunshine.presenter.MainPresenter;
import com.example.android.sunshine.view.settingsFragment.SettingsActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    private final String TAG = MainActivity.class.getSimpleName();

    ForecastAdapterMVP mForecastAdapter;

    @BindView(R.id.recyclerview_forecast)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    //private int mPosition = RecyclerView.NO_POSITION;

    @Inject
    App app;

    //@Inject
    //ApiService api;

    @InjectPresenter
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0f);

        Timber.d("Start Main Activity");
        sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals(getString(R.string.pref_units_key))) {
                Timber.d("Change settings MainActivity");
                updateList();
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Timber.d("register listenerSharedPreference");

        // background task

        //SunshineSyncUtils.initialize(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadData();

    }




    @Override
    public void init() {
        //https://itnext.io/timber-enhancing-your-logging-experience-330e8af97341

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        String message = " linear layout";
        Timber.i("Init Recycler %s", message);
        mForecastAdapter = new ForecastAdapterMVP(presenter.getListPresenter(), this, this);
        Timber.d("Init Recycler - created adapter");
        mRecyclerView.setAdapter(mForecastAdapter);
        Timber.d("Init Recycler - set adapter");
        mRecyclerView.setHasFixedSize(true);
    }
    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates();
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Timber.d("posLat " + posLat);
            Timber.d("posLong " + posLong);
            startActivity(intent);
        } else {
            Timber.tag(TAG).d("Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister the preference change listener
        Timber.d("unregister listenerSharedPreference");
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }
    @ProvidePresenter
    public MainPresenter createPresenter()
    {
        MainPresenter presenter = new MainPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;

    }
    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        //Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.putExtra("dateLong", date);
        //weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }

    public void hideLoading() {
        /* First, hide the loading indicator */
        Timber.d("Loading end - hideLoad");
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        Timber.d("Adapter item count %d", mRecyclerView.getAdapter().getItemCount());
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    public void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.

     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void updateList() {
        mForecastAdapter.notifyDataSetChanged();
    }

}
