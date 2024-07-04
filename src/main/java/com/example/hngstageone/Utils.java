package com.example.hngstageone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class Utils {



//    @Value("${google.location.apikey}")
//    private String googleApiKey;

    private static final String GOOGLE_APIKEY = System.getenv("googleKey");


    public  JsonNode getLocationCoordinates() throws URISyntaxException, IOException, InterruptedException {
        String baseUrl = "https://www.googleapis.com/geolocation/v1/geolocate?key="+ GOOGLE_APIKEY;
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{}";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(jsonBody)).uri(new URI(baseUrl)).header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode object = objectMapper.readTree(response.body());
            return object.get("location");
        } else {
            System.err.println("HTTP request failed with status code: " + response.statusCode());
            throw new IOException("Failed to get location coordinates");
        }
    }


    public static String extractCountryNameFromJson(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            System.out.println("City = " + jsonNode.get("city").asText());
            return jsonNode.get("city").asText();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    public static String extractWeatherFromJson(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode currentWeatherNode = rootNode.path("current_weather");
        return currentWeatherNode.path("temperature").asText();
    }


}
