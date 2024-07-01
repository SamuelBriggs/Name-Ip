package com.example.hngstageone.service;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


public interface StageOneService {

    String getClientIp(HttpServletRequest request);

    String getLocation(HttpServletRequest request) throws IOException, URISyntaxException;

    String getWeather(double latitude, double longitude) throws URISyntaxException, IOException, InterruptedException;
}
