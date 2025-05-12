package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import services.TestModeContext;

public class HomeController {

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            LayoutController layoutController = LayoutController.getInstance();
            layoutController.setContent(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void startExamMode(ActionEvent event) {
        TestModeContext.getInstance().setMode(TestModeContext.Mode.EXAM);
        loadContent("/views/main-view.fxml");
    }

    @FXML
    void startPracticeMode(ActionEvent event) {
        TestModeContext.getInstance().setMode(TestModeContext.Mode.PRACTICE);
        loadContent("/views/main-view.fxml");
    }

    @FXML
    void startTopicsMode(ActionEvent event) {
        loadContent("/views/topics-view.fxml");
    }

    @FXML
    void startMistakesMode(ActionEvent event) {
        loadContent("/views/my-mistakes-view.fxml");
    }

    @FXML
    void startFrequentMistakesMode(ActionEvent event) {
        System.out.println("Найскладніші питання - ще не реалізовано");
    }

    @FXML
    void startSavedMode(ActionEvent event) {
        System.out.println("Збережені питання - ще не реалізовано");
    }
}
