package com.example.android.sunshine.view;

public interface ListRowView
{
    void setWeatherIcon(int weatherId);
    void setDate(long  dateInMillis);
    void setDescription(int weatherId);
    void setHighTempView(double highInCelsius);
    void setMinTempView(double lowInCelsius);

    int getPos();
}
