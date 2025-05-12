package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import models.ResultModel;

public class ResultController {

    @FXML
    private Label summaryLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label titleLabel;

    private ResultModel result;

    public void setResult(ResultModel result) {
        this.result = result;

        if (result.isFailed()) {
            titleLabel.setText("Тест не здано");
            summaryLabel.setText("Ви допустили більше 2 помилок.");
            timeLabel.setText("Час до провалу: " + result.getFormattedTime());
        } else {
            summaryLabel.setText("Ви відповіли правильно на " + result.getCorrect() + " з " + result.getTotal());
            timeLabel.setText("Час проходження: " + result.getFormattedTime());
        }
    }

    @FXML
    void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home-view.fxml"));
            Parent view = loader.load();
            LayoutController.getInstance().setContent(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void reviewMistakes() {
        System.out.println("Функціонал перегляду помилок — ще не реалізовано.");
    }
}
