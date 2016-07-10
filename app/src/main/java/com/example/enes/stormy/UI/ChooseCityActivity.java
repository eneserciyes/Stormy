package com.example.enes.stormy.UI;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.enes.stormy.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseCityActivity extends AppCompatActivity {

    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    Spinner mCountrySpinner;
    Spinner mCitySpinner;
    private String city;
    private double mLatitude;
    private double mLongitude;
    private CitiesAndCountries mCitiesAndCountries;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        mCountrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        mCitySpinner = (Spinner) findViewById(R.id.citySpinner);

        mCitiesAndCountries.populateMap();

        Map<String,String[]> cities = mCitiesAndCountries.getCities();

    }

    private void getLatLang(){
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
                            mLatitude;
                            mLongitude;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ChooseCityActivity.this,MainActivity.class);
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
                    }catch (JSONException e){
                        Log.e(MainActivity.TAG,"Exception caught: ",e);
                    }
                }
            });
        }else{
            Toast.makeText(this, R.string.error_network,
                    Toast.LENGTH_LONG ).show();
        }

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
