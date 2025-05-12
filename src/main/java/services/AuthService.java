package services;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AuthService {
    private String userId;
    private String accessToken;

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_URL = dotenv.get("SUPABASE_URL") + "/auth/v1";
    private static final String API_KEY = dotenv.get("SUPABASE_API_KEY");

    private final HttpClient client = HttpClient.newHttpClient();

    public String register(String email, String password) throws Exception {
        String json = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/signup"))
                .header("apikey", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private String extractToken(String responseBody) {
        try {
            int start = responseBody.indexOf("\"access_token\":\"") + 16;
            int end = responseBody.indexOf("\"", start);
            return responseBody.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public String login(String email, String password) throws Exception {
        String json = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/token?grant_type=password"))
                .header("apikey", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String body = response.body();
            this.accessToken = extractField(body, "access_token");
            this.userId = extractField(body, "user", "id");
        }

        return response.body();
    }

    private String extractField(String body, String key) {
        int start = body.indexOf("\"" + key + "\":\"") + key.length() + 4;
        int end = body.indexOf("\"", start);
        return body.substring(start, end);
    }

    private String extractField(String body, String objectKey, String nestedKey) {
        int objectStart = body.indexOf("\"" + objectKey + "\":{");
        int keyStart = body.indexOf("\"" + nestedKey + "\":\"", objectStart) + nestedKey.length() + 4;
        int keyEnd = body.indexOf("\"", keyStart);
        return body.substring(keyStart, keyEnd);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }
}
