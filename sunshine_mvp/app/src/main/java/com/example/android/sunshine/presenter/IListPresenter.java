package com.example.android.sunshine.presenter;


import com.example.android.sunshine.view.ListRowView;

public interface IListPresenter
{
    void bindView(ListRowView holder);
    int getItemCount();
    long getLongDate(int position);

}
