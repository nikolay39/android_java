package com.example.android.sunshine.model.image;

public interface IImageLoader<T>
{
    void loadInto(String url, T container);
}
