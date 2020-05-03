package com.example.android.classicalmusicquiz;

import android.app.Application;
import android.content.Context;


public class App extends Application
{
    private static App instance;
    private static Context context;



    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        App.context = getApplicationContext();
   }

    public static App getInstance(){
        return instance;
    }

    public static Context getAppContext() {
        return App.context;
    }


}
