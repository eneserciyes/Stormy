package com.example.enes.stormy.UI;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.enes.stormy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseCityActivity extends AppCompatActivity{

    public static final String KEY_LATITUDE = "key_latitude";
    public static final String KEY_LONGITUDE = "key_longitude";
    public static final String KEY_CITY = "key_city";
    public static final String KEY_COUNTRY = "key_country";
    private double mLatitude;
    private double mLongitude;
    private Spinner mCitySpinner;
    private Spinner mCountrySpinner;
    public ArrayList<String> country_options;
    public ArrayList<String> city_options;
    public Button mNextButton;
    private String mCitySelected;
    private String mCountrySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        mCitySpinner = (Spinner) findViewById(R.id.city);
        mCountrySpinner = (Spinner) findViewById(R.id.country);
        mNextButton = (Button) findViewById(R.id.nextButton);

        country_options= new ArrayList<>();
        city_options= new ArrayList<>();

        country_options.add("Turkey");
        country_options.add("France");
        country_options.add("USA");

        city_options.add("Niğde");
        city_options.add("Kayseri");
        city_options.add("Sivas");

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, country_options);
        mCountrySpinner.setAdapter(countryAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, city_options);
        mCitySpinner.setAdapter(cityAdapter);

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountrySelected = country_options.get(position);
                resetCity(mCountrySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCitySelected = city_options.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLang(mCitySelected);
            }
        });


    }

    public void resetCity(String countryName) {
        city_options.clear();
        switch (countryName) {
            case "Turkey":
                city_options.add("Sivas");
                city_options.add("Kayseri");
                city_options.add("Niğde");
                break;
            case "France":
                city_options.add("Paris");
                city_options.add("Saint-Etienne");
                city_options.add("Toulouse");
                break;
            case "USA":
                city_options.add("New York");
                city_options.add("Los Angeles");
                city_options.add("Boston");
                break;
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(ChooseCityActivity.this, android.R.layout.simple_spinner_item, city_options);
        mCitySpinner.setAdapter(cityAdapter);
    }

    private void getLatLang(String city){
        String apiKey = "AIzaSyC7j_yvAvebICmRRGwZdHueqL5XRdtYcpE";
        String forecastUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="+city+"&key="+apiKey;

        if(NetworkAvailable()){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if(response.isSuccessful()){
                            parseJSONData(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ChooseCityActivity.this,MainActivity.class);
                                    intent.putExtra(KEY_CITY,mCitySelected);
                                    intent.putExtra(KEY_COUNTRY,mCountrySelected);
                                    intent.putExtra(KEY_LATITUDE,mLatitude);
                                    intent.putExtra(KEY_LONGITUDE,mLongitude);
                                    startActivity(intent);
                                }
                            });
                        }else{
                            alertUserAboutError();
                        }
                    }catch (IOException ioe){
                        Log.e(MainActivity.TAG, "Exception caught: ", ioe);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            Toast.makeText(this, R.string.error_network,
                    Toast.LENGTH_LONG ).show();
        }

    }

    private void parseJSONData(String jsonData) throws JSONException {
        JSONObject geocode = new JSONObject(jsonData);
        JSONArray results = geocode.getJSONArray("results");
        JSONObject result0 = results.getJSONObject(0);
        JSONObject geometry = result0.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        mLatitude = location.getDouble("lat");
        Log.d(MainActivity.TAG,mLatitude+"");
        mLongitude = location.getDouble("lng");
    }

    private boolean NetworkAvailable(){
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




}
