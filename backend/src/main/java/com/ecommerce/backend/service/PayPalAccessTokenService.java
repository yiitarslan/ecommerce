package com.ecommerce.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * ✅ PayPalAccessTokenService — PayPal API'den access token almak için kullanılır.
 */
@Service
public class PayPalAccessTokenService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.api.base}")
    private String baseUrl;

    /**
     * 📡 PayPal access token alma metodu.
     */
    public String getAccessToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Basic Auth için clientId:secret base64 encode edilir
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        // POST isteği hazırlanır
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/oauth2/token"))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        // Yanıt alınır
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Hatalı yanıt kontrol edilir
        if (response.statusCode() != 200) {
            throw new RuntimeException("PayPal access token alınamadı. Kod: " + response.statusCode()
                    + " Mesaj: " + response.body());
        }

        // JSON parse ve access_token çekilir
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.body());

        return jsonNode.get("access_token").asText();
    }
}
