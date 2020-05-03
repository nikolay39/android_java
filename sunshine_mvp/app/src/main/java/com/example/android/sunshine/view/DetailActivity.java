/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.android.sunshine.App;
import com.example.android.sunshine.R;
import com.example.android.sunshine.databinding.ActivityDetailBinding;
import com.example.android.sunshine.model.api.ApiService;
import com.example.android.sunshine.model.common.SunshineDateUtils;
import com.example.android.sunshine.model.common.SunshineWeatherUtils;
import com.example.android.sunshine.model.repo.ForecastRepo;
import com.example.android.sunshine.presenter.DetailPresenter;
import com.example.android.sunshine.view.settingsFragment.SettingsActivity;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class DetailActivity extends MvpAppCompatActivity implements DetailView {
    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";



    /*
     * This ID will be used to identify the Loader responsible for loading the weather details
     * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
     * our case, there is only one. We will still use this ID to initialize the loader and create
     * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
     * whatever number you like, so long as it is unique and consistent.
     */
    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;



    /* The URI that is used to access the chosen day's weather details */
    private long mCurrentDateLong;
    @InjectPresenter
    DetailPresenter presenter;
    @Inject
    App app;

    //@Inject
    //ApiService api;


    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mCurrentDateLong = getIntent().getLongExtra("dateLong", 0);
        presenter.loadForecast(mCurrentDateLong);
        sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (key.equals(getString(R.string.pref_units_key))) {
                Timber.d("Change settings DetailActivity");
                presenter.loadForecast(mCurrentDateLong);
            }
        };

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Timber.d("register listenerSharedPreference DetailActivity");

        //if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        /* This connects our Activity into the loader lifecycle. */
        //getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister the preference change listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Timber.d("unregister listenerSharedPreference DetailActivity");
    }
    @ProvidePresenter
    public DetailPresenter createPresenter()
    {
        DetailPresenter presenter = new DetailPresenter(AndroidSchedulers.mainThread());
        App.getInstance().getAppComponent().inject(presenter);
        return presenter;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }
    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }
    @Override
    public void setWeatherIcon(int weatherId) {
        /* Use our utility method to determine the resource ID for the proper art */
        int weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        /* Set the resource ID on the icon to display the art */
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

    }
    @Override
    public void setDate(long  dateInMillis) {
        /* Read date from the cursor */
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(this, dateInMillis, true);
        mDetailBinding.primaryInfo.date.setText(dateString);
    }
    @Override
    public void setDescription(int weatherId)
    {
        String description = SunshineWeatherUtils.getStringForWeatherCondition(weatherId);
        /* Create the accessibility (a11y) String from the weather description */
        String descriptionA11y = getString(R.string.a11y_forecast, description);
        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);
    }
    public void setHighTempView(double highInCelsius) {
        /* Read high temperature from the cursor (in degrees celsius) */
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String highString = SunshineWeatherUtils.formatTemperature(highInCelsius);
        /* Create the accessibility (a11y) String from the weather description */
        String highA11y = getString(R.string.a11y_high_temp, highString);
        Timber.d("Hight temp" + highString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);
    }
    @Override
    public void setMinTempView(double lowInCelsius) {
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        String lowString = SunshineWeatherUtils.formatTemperature(lowInCelsius);
        String lowA11y = getString(R.string.a11y_low_temp, lowString);


        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);
    }
    @Override
    public void setHumidity(double humidity) {
        /* Read humidity from the cursor */
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.invalidateAll();
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);
    }
    @Override
    public void setWindMeasurement(double windSpeed, double windDirection) {
        String windString = SunshineWeatherUtils.getFormattedWind(windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

    }
    @Override
    public void setPressure(double pressure)  {
        String pressureString = getString(R.string.format_pressure, pressure);

        String pressureA11y = getString(R.string.a11y_pressure, pressureString);

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);

        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);

    }
    public void setForecastSummary() {
        mForecastSummary = mForecastSummary = String.format("%s - %s - %s/%s",
                mDetailBinding.primaryInfo.date.getText(),
                mDetailBinding.primaryInfo.weatherDescription.getText(),
                mDetailBinding.primaryInfo.highTemperature.getText(),
                mDetailBinding.primaryInfo.lowTemperature.getText());
    }



}