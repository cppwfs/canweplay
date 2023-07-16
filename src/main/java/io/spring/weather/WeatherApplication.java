package io.spring.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.spring.weather.entities.ActivityAdvice;
import io.spring.weather.entities.Current;
import io.spring.weather.entities.RequestCategory;
import io.spring.weather.entities.Weather;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SpringBootApplication
public class WeatherApplication {
	private static final Log logger = LogFactory.getLog(WeatherApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}
	private Map<String, String> conditionCode = new HashMap<>();

	@Bean
	ApplicationRunner applicationRunner(Function <String, ActivityAdvice> categorize) {
		return new ApplicationRunner() {
			@Override
			public void run(ApplicationArguments args) throws Exception {
				conditionCode.put("1000", "Sunny");
				conditionCode.put("1003", "Partly cloudy");
				conditionCode.put("1006", "Cloudy");
				conditionCode.put("1009", "Overcast");
				conditionCode.put("1030", "Misty");
			}
		};
	}

	@Bean
	public Function<ActivityAdvice, ActivityAdvice> weather(WeatherProperties properties) {
		return s -> {
			if (properties.getWeatherApiKey() == null) { // If key isn't provided give fake data
				Current current = new Current();
				s.setWeather(new Weather(null, current, null));
				s.getWeather().current().temp_f=107;
				s.getWeather().current().wind_mph=10;
				return s;
			}
			RestTemplate restTemplate  = new RestTemplate();
			String forecast = restTemplate.getForObject(
					"http://api.weatherapi.com/v1/forecast.json?key=" + properties.getWeatherApiKey()+"&q=" + s.getLocation() +
							"&days=1&aqi=no&alerts=no", String.class);

			ObjectMapper om = new ObjectMapper();
			Weather weather;
			try {
				weather = om.readValue(forecast, Weather.class);
				s.setWeather(weather);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		return  s;
		};
	}

	@Bean
	public Function<String, ActivityAdvice> categorize(WeatherProperties properties) {
		return s -> {
			if (properties.getOpenAPIKey() == null) { // If key isn't provided give fake data
				return new ActivityAdvice("Walking", "Las Vegas", "Outdoor", "imperial");
			}
			ChatMessage chatMessage = new ChatMessage("user",
					"categorize for activity and location,sport type( indoor or outdoor), country unit system in json for the following: \"" + s + "\" result should only be in json");
			ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
					.builder()
					.model("gpt-3.5-turbo-0613")
					.messages(Collections.singletonList(chatMessage))
					.maxTokens(256)
					.build();
			OpenAiService service = new OpenAiService(properties.getOpenAPIKey());

			ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

			ObjectMapper om = new ObjectMapper();
			RequestCategory requestCategory;
			try {
				requestCategory = om.readValue(responseMessage.getContent(), RequestCategory.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			ActivityAdvice activityAdvice = new ActivityAdvice(requestCategory.activity(),
					requestCategory.location(), requestCategory.sport_type(), requestCategory.unit_system());

			logger.info(responseMessage.getContent());
			return activityAdvice;
		};
	}

	@Bean
	public Function<ActivityAdvice, String> canWePlay() {
		return advice -> {
			String result;
			if(advice.getSportType().equalsIgnoreCase("Indoor")) {
				result = advice.getActivity() + " can certainly be played today because its an indoor sport!";
			}
			else if (advice.getWeather().current().temp_f > 85 || advice.getWeather().current().temp_f < 50) {
				result = "The temperature isn't right for " + advice.getActivity() + " today, the temperature is " +
						(advice.getUnitSystem().equals("metric") ?
								advice.getWeather().current().temp_c : advice.getWeather().current().temp_f) + ".";
			} else if (advice.getWeather().current().wind_mph > 15) {
				result = "It's too windy for " + advice.getActivity() + " today, the wind speed is " +
						(advice.getUnitSystem().equals("metric") ? advice.getWeather().current().wind_kph : advice.getWeather().current().wind_mph) + ".";
			} else {
				result = "It's a great day to go " + advice.getActivity() + " in "+ advice.getLocation() +" the wind speed is " + advice.getWeather().current().wind_mph +
						" mph and the temperature is " + advice.getWeather().current().temp_f + ".";
			}
			logger.info("==>" + result);
			return result;
		};
	}
}
