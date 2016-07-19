package com.example.enes.stormy.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enes.stormy.R;
import com.example.enes.stormy.weather.Current;
import com.example.enes.stormy.weather.Day;
import com.example.enes.stormy.weather.Forecast;
import com.example.enes.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.enes.stormy.UI.ChooseCityActivity.KEY_CITY;
import static com.example.enes.stormy.UI.ChooseCityActivity.KEY_COUNTRY;
import static com.example.enes.stormy.UI.ChooseCityActivity.KEY_LATITUDE;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String Daily_Forecast = "DAILY_FORECAST";
    public static final String Hourly_Forecast = "HOURLY_FORECAST";
    private Forecast mForecast;

    TextView mTemperatureLabel;
    TextView mTimeLabel;
    TextView mSummaryLabel;
    TextView mHumidityValue;
    TextView mPrecipValue;
    ImageView mIconImageView;
    ImageView mRefreshButton;
    ProgressBar mSpinnerView;
    Button mDailyButton;
    Button mHourlyButton;
    TextView mLocationLabel;
    private double mLongitude;
    private double mLatitude;
    private String mCountry;
    private String mCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationLabel = (TextView) findViewById(R.id.locationLabel);
        mTemperatureLabel =(TextView) findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView) findViewById(R.id.timeLabel);
        mSummaryLabel  = (TextView) findViewById(R.id.summaryLabel);
        mHumidityValue  = (TextView) findViewById(R.id.humidityValue);
        mPrecipValue = (TextView) findViewById(R.id.precipValue);
        mIconImageView  = (ImageView) findViewById(R.id.iconImageView);
        mRefreshButton = (ImageView) findViewById(R.id.refreshButton);
        mSpinnerView = (ProgressBar) findViewById(R.id.mSpinnerView);

        mDailyButton = (Button) findViewById(R.id.dailyButton);
        mHourlyButton = (Button) findViewById(R.id.hourlyButton);

        mSpinnerView.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        mLatitude = intent.getDoubleExtra(ChooseCityActivity.KEY_LATITUDE,0);
        mLongitude = intent.getDoubleExtra(ChooseCityActivity.KEY_LONGITUDE,0);
        mCountry = intent.getStringExtra(ChooseCityActivity.KEY_COUNTRY);
        mCity = intent.getStringExtra(ChooseCityActivity.KEY_CITY);

        mDailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDailyActivity();
            }
        });
        mHourlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHourlyActivity();
            }
        });
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(mLatitude,mLongitude);
            }
        });



        getForecast(mLatitude,mLongitude);
    }

    private void startHourlyActivity() {
        Intent intent = new Intent(this,HourlyForecastActivity.class);
        intent.putExtra(Hourly_Forecast,mForecast.getHourlyForecast());
        intent.putExtra(KEY_CITY,mCity);
        intent.putExtra(KEY_COUNTRY,mCountry);
        startActivity(intent);
    }

    private void startDailyActivity() {
        Intent intent = new Intent(this,DailyForecastActivity.class);
        intent.putExtra(Daily_Forecast,mForecast.getDailyForecast());
        intent.putExtra(KEY_CITY,mCity);
        intent.putExtra(KEY_COUNTRY,mCountry);
        startActivity(intent);
    }

    private Forecast parseForecastDetails(String jsonData) throws  JSONException{
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily  =forecast.getJSONObject("daily");

        JSONArray data = daily.getJSONArray("data");
        Day[] days = new Day[data.length()];

        for(int i=0; i< data.length();i++ ){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();
            Log.v(TAG,"dailyData: "+jsonDay.toString());
            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperature(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);
            days[i] = day;
        }
        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];
        for(int i = 0 ; i<data.length();i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();
            Log.v(TAG,"hourly data: "+jsonHour.toString());
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }
        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");
        Log.v(TAG,"current data: "+currently.toString());
        String timezone = forecast.getString("timezone");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        //Log.v(TAG,"humidity: "+currentWeather.getHumidity());
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipitation(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        //Log.v(TAG,"temperature: "+ currentWeather.getTemperature());
        current.setTimeZone(timezone);


        return current;
    }
    private void updateDisplay() {
        mTemperatureLabel.setText(mForecast.getCurrent().getTemperature()+"");
        mLocationLabel.setText(mCity+","+mCountry);
        mTimeLabel.setText(mForecast.getCurrent().getFormattedTime() + " it will be");
        mHumidityValue.setText(mForecast.getCurrent().getHumidity()+"");
        mPrecipValue.setText(mForecast.getCurrent().getPrecipitation()+ "%");
        mSummaryLabel.setText(mForecast.getCurrent().getSummary());
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), mForecast.getCurrent().getIconID(), null);
        mIconImageView.setImageDrawable(drawable);
    }

    public boolean networkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
        }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    private void getForecast(double latitude,double longitude){
        String apiKey ="406e6444fa0979d7c9cdb4fb72250f4c";

        String forecastUrl = "https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+longitude;

        if(networkAvailable()){
            toggleVisibility();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleVisibility();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleVisibility();
                        }
                    });
                    try{
                        String jsonData = response.body().string();
                        if(response.isSuccessful()){
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        }
                        else {
                            alertUserAboutError();
                        }

                    }catch(IOException ioe){
                        Log.e(TAG,"Exception caught: ", ioe);
                    }
                    catch(JSONException e){
                        Log.e(TAG,"Exception caught: ", e);

                    }
                }
            });
        }
        else{
            Toast.makeText(this, R.string.error_network,
                    Toast.LENGTH_LONG ).show();

        }}

    private void toggleVisibility() {
        if(mSpinnerView.getVisibility() == View.INVISIBLE){
            mSpinnerView.setVisibility(View.VISIBLE);
            mRefreshButton.setVisibility(View.INVISIBLE);
        }
        else{
            mSpinnerView.setVisibility(View.INVISIBLE);
            mRefreshButton.setVisibility(View.VISIBLE);
        }
    }


}

