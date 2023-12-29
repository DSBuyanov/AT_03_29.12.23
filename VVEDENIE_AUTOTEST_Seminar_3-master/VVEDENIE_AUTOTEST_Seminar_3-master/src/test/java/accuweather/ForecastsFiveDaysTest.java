package accuweather;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import seminar.accuweather.location.Location;
import seminar.accuweather.weather.Weather;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ForecastsFiveDaysTest extends AccuweatherAbstractTest {

    @ParameterizedTest
    @CsvSource({"50", "52"})
    void testGetResponse(int locationKey) {
        Weather weather = given().queryParam("apikey", getApiKey()).pathParam("locationKey", locationKey)
                .when().get(getBaseUrl() + "/forecasts/v1/daily/5day/{locationKey}")
                .then().statusCode(200).time(lessThan(2000L))
                .extract().response().body().as(Weather.class);
        assertEquals(5, weather.getDailyForecasts().size());
        System.out.println(weather);
    }

    @ParameterizedTest
    @CsvSource({"Samara, 10, Samarai", "Moscow, 5, Moscowai"})
    void testGetLocations(String query, int expectedSize, String expectedName) {
        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("apikey", getApiKey());
        mapQuery.put("q", query);
        List<Location> listLocations = given().queryParams(mapQuery)
                .when().get(getBaseUrl() + "/locations/v1/cities/autocomplete")
                .then().statusCode(200)
                .extract().body().jsonPath().getList(".", Location.class);
        assertAll(() -> assertEquals(expectedSize, listLocations.size()),
                () -> assertEquals(expectedName, listLocations.get(2).getLocalizedName()));
    }
}