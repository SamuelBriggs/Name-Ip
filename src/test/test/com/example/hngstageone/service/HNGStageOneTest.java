package com.example.hngstageone.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@SpringBootTest
public class HNGStageOneTest {

    @Autowired
    private StageOneService stageOneService;

    @Autowired
    private HttpServletRequest request;

    @Test
    public void getClientIp() {
       String clientIp = stageOneService.getClientIp(request);
        System.out.println("IP Address == " + clientIp);
    }

    @Test
    public void getClientLocation() throws IOException, URISyntaxException {
        String clientLocation = stageOneService.getLocation(request);
        System.out.println("Client Location == " + clientLocation);
    }

    @Test
    public void getTemperature() throws IOException, URISyntaxException, InterruptedException {
        String weather = stageOneService.getWeather();
        System.out.println("Weather == " + weather);
    }
}
