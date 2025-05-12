package services;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ResultUploader {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SUPABASE_URL = dotenv.get("SUPABASE_URL") + "/auth/v1";
    private static final String SUPABASE_API_KEY = dotenv.get("SUPABASE_API_KEY");

    private final HttpClient client = HttpClient.newHttpClient();

    public void uploadResult(String userId, String email, int correct, int total, int time, String mistakesJson, String token)
            throws Exception {
        String json = String.format("""
                {
                  "user_id": "%s",
                  "user_email": "%s",
                  "correct_answers": %d,
                  "total_questions": %d,
                  "duration_sec": %d,
                  "mistakes": %s
                }
                """, userId, email, correct, total, time, mistakesJson);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SUPABASE_URL + "/rest/v1/results"))
                .header("apikey", SUPABASE_API_KEY)
                .header("Prefer", "return=representation")
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Upload response: " + response.body());
    }
}
