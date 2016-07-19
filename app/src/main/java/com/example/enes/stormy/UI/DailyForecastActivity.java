package com.example.enes.stormy.UI;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enes.stormy.R;
import com.example.enes.stormy.adapters.DayAdapter;
import com.example.enes.stormy.weather.Day;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {
    private Day mDays[];
    private String mCountry;
    private String mCity;
    private TextView mLocationLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);

        mLocationLabel = (TextView) findViewById(R.id.locationLabel);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.Daily_Forecast);
        mDays = Arrays.copyOf(parcelables,parcelables.length,Day[].class);

        mCountry = intent.getStringExtra(ChooseCityActivity.KEY_COUNTRY);
        mCity = intent.getStringExtra(ChooseCityActivity.KEY_CITY);

        mLocationLabel.setText(mCity+","+mCountry);


        DayAdapter adapter = new DayAdapter(this, mDays);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek =mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String temperature = mDays[position].getTemperature()+"";
        String message = String.format("On %s, temperature will be %s and it will be %s",
                dayOfTheWeek,conditions,temperature);

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}
