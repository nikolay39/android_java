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
package com.example.android.sunshine.model.common.staticWrappers;

import com.example.android.sunshine.model.common.SunshineDateUtils;
import com.example.android.sunshine.model.database.room.ForecastEntry;
import com.example.android.sunshine.model.entity.City;
import com.example.android.sunshine.model.entity.DayForecast;
import com.example.android.sunshine.model.entity.IntervalDaysForecast;
import com.example.android.sunshine.model.entity.Temp;
import com.example.android.sunshine.model.entity.Weather;
import com.example.android.sunshine.model.preference.SunshinePreferences;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenWeatherConverter {


    public static List<ForecastEntry> convertData(IntervalDaysForecast intervalDaysForecast) {
        Timber.d(intervalDaysForecast.getCity().toString());
        Timber.d("\n IntervalForecast");
        List<DayForecast> dayForecastList = intervalDaysForecast.getDayForecasts();
        City city = intervalDaysForecast.getCity();

        double cityLatitude = city.getCoord().getLat();
        double cityLongitude = city.getCoord().getLat();
        SunshinePreferences.setLocationDetails(cityLatitude, cityLongitude);


        //SunshinePreferences.setLocationDetails(context, cityLatitude, cityLongitude);
        int  dayForecastListSize = dayForecastList.size();
        List<ForecastEntry> forecastEntryList= new ArrayList<>();

        /*
         * OWM returns daily forecasts based upon the local time of the city that is being asked
         * for, which means that we need to know the GMT offset to translate this data properly.
         * Since this data is also sent in-order and the first day is always the current day, we're
         * going to take advantage of that to get a nice normalized UTC date for all of our weather.
         */
//        long now = System.currentTimeMillis();
//        long normalizedUtcStartDay = SunshineDateUtils.normalizeDate(now);

        long normalizedUtcStartDay = SunshineDateUtils.getNormalizedUtcDateForToday();


        for (int i = 0; i < dayForecastListSize; i++) {

            long dateTimeMillis;
            double pressure;
            double humidity;
            double windSpeed;
            double windDirection;

            double high;
            double low;

            int weatherId;

            /* Get the JSON object representing the day */
            DayForecast dayForecast = dayForecastList.get(i);

            /*
             * We ignore all the datetime values embedded in the JSON and assume that
             * the values are returned in-order by day (which is not guaranteed to be correct).
             */
            dateTimeMillis = normalizedUtcStartDay + SunshineDateUtils.DAY_IN_MILLIS * i;

            pressure = dayForecast.getPressure();
            humidity = dayForecast.getHumidity();
            windSpeed = dayForecast.getSpeed();
            windDirection = dayForecast.getDeg();

            /*
             * Description is in a child array called "weather", which is 1 element long.
             * That element also contains a weather code.
             */
            List<Weather> weatherList = dayForecast.getWeather();

            Weather wheather = weatherList.get(0);

            weatherId = wheather.getId();

            /*
             * Temperatures are sent by Open Weather Map in a child object called "temp".
             *
             * Editor's Note: Try not to name variables "temp" when working with temperature.
             * It confuses everybody. Temp could easily mean any number of things, including
             * temperature, temporary variable, temporary folder, temporary employee, or many
             * others, and is just a bad variable name.
             */
            Temp temp = dayForecast.getTemp();
            high = temp.getMax();
            low = temp.getMin();

            ForecastEntry forecastEntry = new ForecastEntry(
            dateTimeMillis,
            pressure,
            humidity,
            windSpeed,
            windDirection,
            high,
            low,
            weatherId);



            forecastEntryList.add(forecastEntry);
        }

        return forecastEntryList;
    }
}