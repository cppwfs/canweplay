package io.spring.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("io.spring")
public class WeatherProperties {

    private String location ="Las Vegas, Nevada";

    private String weatherApiKey;

    private String openAPIKey;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeatherApiKey() {
        return weatherApiKey;
    }

    public void setWeatherApiKey(String weatherApiKey) {
        this.weatherApiKey = weatherApiKey;
    }

    public String getOpenAPIKey() {
        return openAPIKey;
    }

    public void setOpenAPIKey(String openAPIKey) {
        this.openAPIKey = openAPIKey;
    }
}
