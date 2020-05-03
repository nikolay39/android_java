package com.example.android.sunshine.di.modules;

import com.example.android.sunshine.model.api.ApiService;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {
    @Singleton
    @Provides
    public ApiService api(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Named("endpoint")
    @Provides
    public String endpoint() {
        return "https://andfun-weather.udacity.com/weather/";
    }
    @Provides
    public Retrofit retrofit(@Named("endpoint") String baseUrl, OkHttpClient client, GsonConverterFactory gsonConverterFactory,
                             RxJava2CallAdapterFactory rxJava2CallAdapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(endpoint())
                .client(client)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(gsonConverterFactory)
                .build();
    }
    @Provides
    public GsonConverterFactory gsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);

    }
    @Provides
    public Gson gson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

    }

    @Provides
    public RxJava2CallAdapterFactory rxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();

    }
    @Provides
    public HttpLoggingInterceptor httploggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;

    }
    @Provides
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }
}

