package com.example.diploma.main.ControllerLayout;


import static com.example.diploma.databinding.FragmentMainBinding.inflate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.diploma.R;
import com.example.diploma.databinding.FragmentMainBinding;
import com.example.diploma.main.Retrofit.APIService;
import com.example.diploma.main.Weather.WeatherDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {

    String TAG = "WEATHER";
    private LocationManager locationManager;
    APIService.ApiInterface api;
    private FragmentMainBinding binding;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        api = APIService.getClient().create(APIService.ApiInterface.class);
        // Текущее время
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.forLanguageTag("ru"));
        String dateText = dateFormat.format(currentDate);

        DateFormat weekdayFormat = new SimpleDateFormat("EEE", Locale.forLanguageTag("ru"));
        String weekdayText = weekdayFormat.format(currentDate);

        binding.dateTextView.setText(dateText);
        binding.weekdayTextView.setText(weekdayText);
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            }
            else {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        102);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                    locationListener);
        }
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
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
        String key = requireActivity().getString(R.string.API_KEY);

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
                        binding.tepmeratuteTextView.setText(data.getTempWithDegree() + "C");
                        binding.address.setText(data.getCity());
                        Glide.with(MainFragment.this)
                                .load(data.getIconUrl())
                                .error(R.drawable.weather)
                                .into(binding.weatherImageView);
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