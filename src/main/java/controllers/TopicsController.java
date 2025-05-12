package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Question;
import services.SupabaseQuestionService;
import services.TestModeContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TopicsController {

    @FXML
    private VBox themesBox;

    private final SupabaseQuestionService questionService = new SupabaseQuestionService();

    @FXML
    public void initialize() {
        Map<String, List<Question>> grouped = questionService.getQuestionsGroupedByTheme();

        for (String theme : grouped.keySet()) {
            Button button = new Button(theme);
            button.getStyleClass().add("section-button");

            button.setOnAction(e -> {
                TestModeContext.getInstance().setMode(TestModeContext.Mode.TOPIC);
                TestModeContext.getInstance().setQuestions(grouped.get(theme));
                goToMain();
            });

            themesBox.getChildren().add(button);
        }
    }

    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
            Parent view = loader.load();
            LayoutController.getInstance().setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
