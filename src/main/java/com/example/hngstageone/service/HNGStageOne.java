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

import static com.example.hngstageone.Utils.*;


@Service
public class HNGStageOne implements StageOneService {


    @Override
    public String getClientIp(HttpServletRequest request) {
        String ipAddressFromProxy = request.getHeader("X-Forward-For");
        if (ipAddressFromProxy == null || ipAddressFromProxy.isEmpty()){
//            System.out.println("Res == " + request.getHeader("X-Real-IP"));
            return request.getRemoteAddr();
        }
        return ipAddressFromProxy;
    }


    public String getLocation(HttpServletRequest request) throws URISyntaxException, IOException {
        String ipAddress = getClientIp(request);
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
            }
        } catch (IOException e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            throw e;
        } finally {
            httpClient.close();
        }

        return null;
    }


    public String getWeather() throws URISyntaxException, IOException, InterruptedException {
        JsonNode object = getLocationCoordinates();

        double latitude = object.get("lat").asDouble();
        double longitude = object.get("lng").asDouble();

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
            }
        } catch (IOException e) {
            System.err.println("HTTP request failed: " + e.getMessage());
            throw e;
        } finally {
            httpClient.close();
        }

        return null;
    }


}
