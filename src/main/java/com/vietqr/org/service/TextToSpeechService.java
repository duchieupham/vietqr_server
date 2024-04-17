package com.vietqr.org.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class TextToSpeechService {
    private static final Logger logger = Logger.getLogger(TextToSpeechService.class);

    private static final String API_URL = "https://vbee.vn/api/v1/tts";
    private static final String APP_ID = "ea34e6cc-b943-452d-9882-b5306adda4e1";
    private static final String AUTH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2ODIzMTI0NTB9.eKmnpAPrPhaak9o-ii0GfWKlW2TTm2fcbqaZWLouLJc";
    private static Map<String, String> tts = new HashMap<>();
    private static Map<String, Map<String, String>> ttsData = new HashMap<>();

    // hàm insert key
    public void insert(String key, String value, Map<String, String> data) {
        tts.put(key, value);
        ttsData.put(key, data);
    }

    public String find(String key) {
        return tts.get(key);
    }

    public Map<String, String> findData(String key) {
        return ttsData.get(key);
    }

    // hàm update value

    public boolean update(String key, String value) {
        boolean result = false;
        if (tts.containsKey(key) && ttsData.containsKey(key)) {
            tts.put(key, value);
            result = true;
        }
        return result;
    }

    public boolean updateWithData(String key, String value, Map<String, String> data) {
        boolean result = false;
        if (tts.containsKey(key) && ttsData.containsKey(key)) {
            tts.put(key, value);
            ttsData.put(key, data);
            result = true;
        }
        return result;
    }

    //
    public void delete(String key) {
        tts.remove(key);
        ttsData.remove(key);
    }

    public String requestTTS(String userId, Map<String, String> notiData, String amount) {
        String result = "";
        try {
            // Build the request body
            String callback_url = "https://api.vietqr.org/vqr/api/transaction/voice/" + userId;
            String input_text = amount.trim() + " đồng";
            String voice_code = "hn_female_ngochuyen_full_48k-fhg";
            String audio_type = "mp3";
            int bitrate = 128;
            String speed_rate = "1.0";
            Map<String, Object> data = new HashMap<>();
            data.put("app_id", APP_ID);
            data.put("callback_url", callback_url);
            data.put("input_text", input_text);
            data.put("voice_code", voice_code);
            data.put("audio_type", audio_type);
            data.put("bitrate", bitrate);
            data.put("speed_rate", speed_rate);
            // Build the request URL with query parameters
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(API_URL).build();

            // Create a WebClient
            WebClient webClient = WebClient.builder().baseUrl(uriComponents.toUriString()).build();

            // Send the POST request with headers and request body
            Mono<ClientResponse> responseMono = webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("app_id", APP_ID)
                            .queryParam("callback_url", callback_url)
                            .queryParam("input_text", input_text)
                            .queryParam("voice_code", voice_code)
                            .queryParam("audio_type", audio_type)
                            .queryParam("bitrate", bitrate)
                            .queryParam("speed_rate", speed_rate)
                            .build())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + AUTH_TOKEN)
                    .body(BodyInserters.fromValue(data))
                    .exchange();
            ClientResponse response = responseMono.block();
            logger.info("TTS: status code: " + response.statusCode());
            if (response.statusCode().is2xxSuccessful()) {
                String json = response.bodyToMono(String.class).block();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(json);
                if (rootNode.get("result") != null) {
                    String requestId = rootNode.get("result").get("request_id").asText();
                    insert(requestId, "", notiData);
                    // result = requestId;
                } else {
                    logger.info("TTS: empty result: " + json);
                }
            }
        } catch (Exception e) {
            logger.error("TTS: Error " + e.toString());
        }
        return result;
    }

}
