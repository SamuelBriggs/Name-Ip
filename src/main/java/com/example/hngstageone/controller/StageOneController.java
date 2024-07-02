package com.example.hngstageone.controller;

import com.example.hngstageone.service.StageOneService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class StageOneController {

    @Autowired
    private StageOneService stageOneService;

    @GetMapping("/api/hello")
    public ResponseEntity<?> helloVisitor(@RequestParam String visitorName, HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        Map<String, Object> response = new HashMap<>();
        String clientIp = stageOneService.getClientIp(request);
        String location = stageOneService.getLocation(request);
        String temperature = stageOneService.getWeather();

        response.put("client_ip", clientIp);
        response.put("greeting", String.format("Hello, %s! the temperature is %s degree Celcius in %s", visitorName, temperature, location));
        response.put("location", location);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
