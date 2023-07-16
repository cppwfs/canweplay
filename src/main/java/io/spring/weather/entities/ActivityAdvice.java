package io.spring.weather.entities;

public class ActivityAdvice {
    private String activity;
    private String location;

    private Weather weather;
    private String advice;

    private String sportType;

    private String unitSystem;

    public ActivityAdvice() {
    }

    public ActivityAdvice(String activity, String location, String sportType, String unitSystem) {
        this.activity = activity;
        this.location = location;
        this.sportType = sportType;
        this.unitSystem = unitSystem;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getUnitSystem() {
        return unitSystem;
    }

    public void setUnitSystem(String unitSystem) {
        this.unitSystem = unitSystem;
    }
}
