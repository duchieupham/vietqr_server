package com.vietqr.org.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessengerServiceImpl implements MessengerService{

    @Value("${messenger.page.access.token}")
    private String pageAccessToken;
    private final String GRAPH_API_URL = "https://graph.facebook.com/v20.0/PAGE-ID//messages?access_token=";

    @Override
    public void sendMessage(String recipientId, String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String jsonBody = "{"
                + "\"recipient\": {\"id\": \"" + recipientId + "\"},"
                + "\"message\": {\"text\": \"" + message + "\"}"
                + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        String url = GRAPH_API_URL + pageAccessToken;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        System.out.println("Response from Facebook: " + response.getBody());
    }
}
