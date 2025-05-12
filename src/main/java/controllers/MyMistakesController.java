package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import models.Question;
import services.AuthContext;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MyMistakesController {

    @FXML
    private ListView<String> mistakeListView;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        try {
            String userId = AuthContext.getInstance().getUserId();
            String token = AuthContext.getInstance().getAccessToken();
            String url = "https://ggxbivxwcesrnhfagrti.supabase.co/rest/v1/results?user_id=eq." + userId + "&order=created_at.desc&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", "...")
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            if (!responseBody.startsWith("[")) return;

            ObjectMapper mapper = new ObjectMapper();
            List<?> resultList = mapper.readValue(responseBody, List.class);
            if (resultList.isEmpty()) return;

            var lastResult = (java.util.Map<?, ?>) resultList.get(0);
            List<Integer> mistakeIndices = mapper.readValue(
                    lastResult.get("mistakes").toString(),
                    new TypeReference<>() {}
            );

            InputStream inputStream = getClass().getResourceAsStream("/questions.json");
            List<Question> allQuestions = mapper.readValue(inputStream, new TypeReference<>() {});

            List<String> mistakeQuestions = new ArrayList<>();
            for (Integer index : mistakeIndices) {
                if (index < allQuestions.size()) {
                    Question q = allQuestions.get(index);
                    mistakeQuestions.add("❌ " + q.getQuestionText());
                }
            }

            mistakeListView.getItems().addAll(mistakeQuestions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mistakeListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
