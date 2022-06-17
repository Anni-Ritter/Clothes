package com.example.diploma.main.Weather;

import com.google.gson.annotations.SerializedName;
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
    private final List<WeatherDescription> description;

    @SerializedName("name")
    private String city;

    public WeatherDay(WeatherTemp temp, List<WeatherDescription> description) {
        this.temp = temp;
        this.description = description;
    }

    public String getTempWithDegree() { return String.valueOf(temp.temp.intValue()) + "\u00B0"; }

    public String getCity() { return city; }

    public String getIcon() { return description.get(0).icon; }

    public String getIconUrl() {
        return "https://openweathermap.org/img/w/" + description.get(0).icon + ".png";
    }
}
