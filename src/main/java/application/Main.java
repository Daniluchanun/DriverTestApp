package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Question;
import services.QuestionCache;
import services.SectionCache;
import services.SupabaseQuestionService;

import java.util.List;
import java.util.Map;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        SupabaseQuestionService questionService = new SupabaseQuestionService();
        List<Question> allQuestions = questionService.fetchAllQuestions();
        QuestionCache.setQuestions(questionService.fetchAllQuestions());

        List<Map<String, Object>> sections = new SupabaseQuestionService().fetchAllSections();
        SectionCache.setSections(sections);

        System.out.println("📚 Завантажено тем: " + sections.size());

        System.out.println("📥 Кешовано питань: " + allQuestions.size());

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/layout-view.fxml"));
            primaryStage.setTitle("Тест для майбутніх водіїв");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
