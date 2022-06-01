package com.example.diploma.main.Weather;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeatherDay {
    public static class WeatherTemp {
        Double temp;
    }

    public static class WeatherDescription {
        String icon;
    }


    @SerializedName("main")
    private final WeatherTemp temp;

    @SerializedName("weather")
    private final List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long time;


    public WeatherDay(WeatherTemp temp, List<WeatherDescription> desctiption) {
        this.temp = temp;
        this.desctiption = desctiption;
    }

    public long getDate(){
        return time;
    }

    public String getTempWithDegree() { return String.valueOf(temp.temp.intValue()) + "\u00B0"; }

    public String getCity() { return city; }

    public String getIcon() { return desctiption.get(0).icon; }

    public String getIconUrl() {
        return "https://openweathermap.org/img/w/" + desctiption.get(0).icon + ".png";
    }
}
