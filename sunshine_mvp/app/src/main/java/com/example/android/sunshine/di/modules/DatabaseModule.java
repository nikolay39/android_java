package com.example.android.sunshine.di.modules;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.sunshine.model.database.room.DatabaseManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @Named("dbName")
    @Provides
    public String provideDbName() {
        return "sunshine";
    }
    @Singleton
    @Provides
    public DatabaseManager provideRoomDatabase(Context context, @Named("dbName") String dbName) {
        return Room.databaseBuilder(context, DatabaseManager.class, dbName).build();
    }


}
