package com.example.diploma.main.ControllerLayout;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.diploma.R;
import com.example.diploma.databinding.FragmentMainBinding;
import com.example.diploma.main.Retrofit.APIService;
import com.example.diploma.main.Weather.WeatherDay;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {

    TextView myAddress;
    TextView date;
    TextView weekday;
    TextView temperature;
    ImageView weatherImg;
    String TAG = "WEATHER";
    private LocationManager locationManager;
    APIService.ApiInterface api;
    private FragmentMainBinding binding;
    private SharedPreferences sPref;
    final String WEATHER_TEXT = "saved_text";
    final String CITY_TEXT = "saved_text";
    final String WEEKDAY_TEXT = "saved_text";
    final String IMAGE_URL = "saved_text";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        date = rootView.findViewById(R.id.dateTextView);
        weekday = rootView.findViewById(R.id.weekdayTextView);
        myAddress = rootView.findViewById(R.id.address);
        temperature = rootView.findViewById(R.id.tepmeratuteTextView);
        weatherImg = rootView.findViewById(R.id.weatherImageView);
        api = APIService.getClient().create(APIService.ApiInterface.class);


        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                    }
                });

        // Текущее время
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.forLanguageTag("ru"));
        String dateText = dateFormat.format(currentDate);

        DateFormat weekdayFormat = new SimpleDateFormat("EEE", Locale.forLanguageTag("ru"));
        String weekdayText = weekdayFormat.format(currentDate);

        date.setText(dateText);
        weekday.setText(weekdayText);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 10, 10, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            getWeather(location);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            getWeather(locationManager.getLastKnownLocation(provider));
        }

    };

    public void getWeather(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        String units = "metric";
        String lang = "ru";
        String key = APIService.KEY;

        Log.d(TAG, "OK");
        Call<WeatherDay> callToday = api.getToday(lat, lng, units, lang, key);
        callToday.enqueue(new Callback<WeatherDay>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherDay> call, @NonNull Response<WeatherDay> response) {
                Log.e(TAG, "onResponse");
                WeatherDay data = response.body();
                //Log.d(TAG,response.toString());
                if (response.isSuccessful()) {
                    if (data != null) {
                        temperature.setText(data.getTempWithDegree() + "C");
                        myAddress.setText(data.getCity());
                        Glide.with(MainFragment.this)
                                .load(data.getIconUrl())
                                .error(R.drawable.weather)
                                .into(weatherImg);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<WeatherDay> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });
    }
}