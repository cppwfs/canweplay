package io.spring.weather.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RequestCategory(String activity, String location,
                              @JsonProperty("sport_type") @JsonAlias({"sport type", "sportType"}) String sport_type,
                              @JsonProperty("unit_system") @JsonAlias({"unit system", "unitSystem", "country unit system"}) String unit_system,
                              String country) {
}
