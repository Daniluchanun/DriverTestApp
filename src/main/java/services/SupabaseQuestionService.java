package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.cdimascio.dotenv.Dotenv;
import models.Question;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SupabaseQuestionService {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String SUPABASE_URL = dotenv.get("SUPABASE_URL") + "/rest/v1/questions";
    private static final String API_KEY = dotenv.get("SUPABASE_API_KEY");

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Question> fetchAllQuestions() {
        List<Question> allQuestions = new ArrayList<>();
        int pageSize = 1000;
        int offset = 0;

        try {
            while (true) {
                String joinedUrl = SUPABASE_URL + "?select=*,options(text,index)";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(joinedUrl))
                        .header("apikey", API_KEY)
                        .header("Authorization", "Bearer " + API_KEY)
                        .header("Content-Type", "application/json")
                        .header("Range-Unit", "items")
                        .header("Range", offset + "-" + (offset + pageSize - 1))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();

                List<Map<String, Object>> rawList = mapper.readValue(json, new TypeReference<>() {});
                if (rawList.isEmpty()) break;

                for (Map<String, Object> item : rawList) {
                    int id = (int) item.get("id");
                    String text = (String) item.get("question_text");
                    int correct = (int) item.get("correct_answer_index");
                    String theme = (String) item.get("theme");
                    String img = item.get("image_path") != null ? (String) item.get("image_path") : null;

                    List<Map<String, Object>> optionList = (List<Map<String, Object>>) item.get("options");
                    optionList.sort(Comparator.comparingInt(opt -> (Integer) opt.get("index")));

                    List<String> options = new ArrayList<>();
                    for (Map<String, Object> opt : optionList) {
                        options.add((String) opt.get("text"));
                    }

                    allQuestions.add(new Question(id, text, options, correct, theme, img));
                }

                if (rawList.size() < pageSize) break;
                offset += pageSize;
            }

            System.out.println("📦 Загальна кількість питань: " + allQuestions.size());
            return allQuestions;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    public Map<String, List<Question>> getQuestionsGroupedByTheme() {
        List<Question> questions = services.QuestionCache.getQuestions();
        List<Map<String, Object>> sections = services.SectionCache.getSections();

        Map<String, List<Question>> grouped = new LinkedHashMap<>();

        for (Map<String, Object> section : sections) {
            String title = (String) section.get("name");
            if (title != null) {
                String normalizedTitle = title.trim().replaceAll("\\s+", " ");
                List<Question> filtered = questions.stream()
                        .filter(q -> {
                            String theme = q.getTheme();
                            return theme != null && theme.trim().replaceAll("\\s+", " ").equals(normalizedTitle);
                        })
                        .toList();

                if (!filtered.isEmpty()) {
                    grouped.put(normalizedTitle, filtered);
                }
            }
        }

        return grouped;
    }


    public List<Map<String, Object>> fetchAllSections() {
        try {
            String url = "https://ggxbivxwcesrnhfagrti.supabase.co/rest/v1/sections?select=id,name&order=id.asc";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
