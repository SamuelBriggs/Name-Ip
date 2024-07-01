package com.example.hngstageone.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Service
public class HNGStageOne implements StageOneService {

    @Autowired
    private HttpServletRequest request;

    @Override
    public String getClientIp(HttpServletRequest request) {
        String ipAddressFromProxy = request.getHeader("X-Forward-For");
        if (ipAddressFromProxy == null || ipAddressFromProxy.isEmpty()){
//            System.out.println("Res == " + request.getHeader("X-Real-IP"));
            return request.getRemoteAddr();
        }
        return ipAddressFromProxy;
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("header names - " + headerNames.toString() );
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }


    public String getLocation() throws URISyntaxException, IOException {
        URL ipapi = new URL("https://ipapi.co/192.168.0.1/json/");

        URLConnection c = ipapi.openConnection();
        c.setRequestProperty("User-Agent", "java-ipapi-v1.02");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(c.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null)
        {
            System.out.println(line);
        }
        reader.close();
        return  reader.toString();
}

    public String getLocation(HttpServletRequest request) throws URISyntaxException, IOException {
        String ipAddress = "8.8.8.8";
        String uriString = String.format("https://ipapi.co/%s/json/", ipAddress);
        URI ipapiUri = new URI(uriString);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(ipapiUri);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String jsonResponse = EntityUtils.toString(entity);
                    return extractCountryNameFromJson(jsonResponse);
                }
            } else {
                System.err.println("HTTP request failed with status code: " + statusCode);
                // Handle error cases
            }
        } catch (IOException e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            throw e;
        } finally {
            httpClient.close();
        }

        return null;
    }

    private String extractCountryNameFromJson(String jsonResponse) {
        System.out.println(jsonResponse);
        int startIndex = jsonResponse.indexOf("\"country_name\":\"") + "\"country_name\":\"".length();
        System.out.println(startIndex);
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        System.out.println(endIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }

    private String extractCountryFromJson(String jsonResponse) {
        // Implement parsing logic based on your JSON structure
        // Example: assuming JSON structure like {"country":"United States"}
        // Using a JSON parsing library like Jackson or Gson is recommended for robust parsing.
        // For simplicity, a basic approach using substring and indexOf is shown here:
        System.out.println("json res = " + jsonResponse);
        int startIndex = jsonResponse.indexOf("\"country\":\"") + "\"country\":\"".length();
        int endIndex = jsonResponse.indexOf("\"", startIndex);
        return jsonResponse.substring(startIndex, endIndex);
    }


    @Override
    public String getTemperature() {
        return null;
    }

    public String getWeather(double latitud, double longitud) throws URISyntaxException, IOException {
        double latitude = 6.5396437;
        double longitude = 3.3457955;
        String uriString = String.format("https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true", latitude, longitude);
        URI weatherUri = new URI(uriString);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(weatherUri);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String jsonResponse = EntityUtils.toString(entity);
                    return extractWeatherFromJson(jsonResponse);
                }
            } else {
                System.err.println("HTTP request failed with status code: " + statusCode);
                // Handle error cases
            }
        } catch (IOException e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            throw e;
        } finally {
            httpClient.close();
        }

        return null;
    }

    private String extractWeatherFromJson(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode currentWeatherNode = rootNode.path("current_weather");
        return currentWeatherNode.path("temperature").asText();
    }
}
