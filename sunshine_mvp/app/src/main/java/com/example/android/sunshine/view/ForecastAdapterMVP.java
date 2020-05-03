package com.example.android.sunshine.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.R;
import com.example.android.sunshine.model.common.SunshineDateUtils;
import com.example.android.sunshine.model.common.SunshineWeatherUtils;
import com.example.android.sunshine.presenter.IListPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ForecastAdapterMVP extends Adapter<ForecastAdapterMVP.ForecastViewHolder>
{

    IListPresenter presenter;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;


    private boolean mUseTodayLayout;
    private final Context mContext;
    final private MainView mClickHandler;



    public ForecastAdapterMVP(IListPresenter presenter, Context context, MainView mClickHandler)
    {
        this.presenter = presenter;
        this.mContext = context;
        this.mClickHandler = mClickHandler;
        this.mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);


    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                Timber.d("VIEW_TYPE_TODAY layout");
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                Timber.d("VIEW_TYPE_FUTURE_DAY layout");
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        Timber.d("create viewholder");

        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position)
    {
        holder.pos = position;
        holder.viewType = getItemViewType(position);
        presenter.bindView((ListRowView)holder);

    }

    @Override
    public int getItemCount()
    {
        return presenter.getItemCount();
    }
    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            Timber.d("VIEW_TYPE_TODAY getItem");
            return VIEW_TYPE_TODAY;
        } else {
            Timber.d("VIEW_TYPE_FUTURE_DAY getItem");
            return VIEW_TYPE_FUTURE_DAY;
        }
    }
    class ForecastViewHolder extends RecyclerView.ViewHolder implements ListRowView, View.OnClickListener
    {

        @BindView(R.id.weather_icon)
        ImageView idTextView;

        @BindView(R.id.date)
        TextView dateView;

        @BindView(R.id.weather_description)
        TextView descriptionView;

        @BindView(R.id.high_temperature)
        TextView highTempView;

        @BindView(R.id.low_temperature)
        TextView lowTempView;
        int pos;
        int viewType;

        ForecastViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        //
        public void setWeatherIcon(int weatherId) {
            int weatherImageId;


            switch (viewType) {

                case VIEW_TYPE_TODAY:
                    Timber.d("VIEW_TYPE_TODAY Large icon");
                    weatherImageId = SunshineWeatherUtils
                            .getLargeArtResourceIdForWeatherCondition(weatherId);
                    break;

                case VIEW_TYPE_FUTURE_DAY:
                    Timber.d("VIEW_TYPE_FUTURE_DAY small icon");
                    weatherImageId = SunshineWeatherUtils
                            .getSmallArtResourceIdForWeatherCondition(weatherId);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);

            }
            idTextView.setImageResource(weatherImageId);

        }
        @Override
        public void setDate(long  dateInMillis) {
            /* Read date from the cursor */
            /* Get human readable string using our utility method */
            String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
            dateView.setText(dateString);
        }

        @Override
        public void setDescription(int weatherId)
        {
            String description = SunshineWeatherUtils.getStringForWeatherCondition(weatherId);
            /* Create the accessibility (a11y) String from the weather description */
            String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);

            /* Set the text and content description (for accessibility purposes) */
            descriptionView.setText(description);
            descriptionView.setContentDescription(descriptionA11y);
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
            String highA11y = mContext.getString(R.string.a11y_high_temp, highString);


            /* Set the text and content description (for accessibility purposes) */
            highTempView.setText(highString);
            highTempView.setContentDescription(highA11y);
        }
        public void setMinTempView(double lowInCelsius) {
            /*
             * If the user's preference for weather is fahrenheit, formatTemperature will convert
             * the temperature. This method will also append either °C or °F to the temperature
             * String.
             */
            String lowString = SunshineWeatherUtils.formatTemperature(lowInCelsius);
            String lowA11y = mContext.getString(R.string.a11y_low_temp, lowString);

            /* Set the text and content description (for accessibility purposes) */
            lowTempView.setText(lowString);
            lowTempView.setContentDescription(lowA11y);
        }

        @Override
        public int getPos()
        {
            return pos;
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            long dateInMillis = presenter.getLongDate(adapterPosition);

            //mCursor.moveToPosition(adapterPosition);
            // index_weather_date
            mClickHandler.onClick(dateInMillis);
        }
    }
}
