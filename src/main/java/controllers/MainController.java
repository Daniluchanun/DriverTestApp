package controllers;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Question;
import models.ResultModel;
import services.QuestionCache;
import services.SupabaseQuestionService;
import services.TestModeContext;


import java.io.IOException;
import java.util.*;


public class MainController {

    @FXML
    private Label questionLabel;

    @FXML
    private VBox optionsBox;

    @FXML
    private Label timerLabel;

    @FXML
    private HBox questionTiles;

    @FXML
    private ImageView questionImage;


    private final SupabaseQuestionService questionService = new SupabaseQuestionService();
    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private ToggleGroup optionsGroup;
    private int correctAnswers = 0;
    private int mistakeCount = 0;
    private final List<Integer> mistakeIds = new ArrayList<>();

    private final Set<Integer> answeredCorrectly = new HashSet<>();
    private final Set<Integer> answeredIncorrectly = new HashSet<>();


    private Timeline timeline;
    private int timeSeconds = 0;

    private TestModeContext.Mode currentMode;

    private final Map<Integer, Integer> selectedAnswers = new HashMap<>();

    private static final int MAX_EXAM_MISTAKES = 2;
    private static final int MAX_EXAM_TIME_SECONDS = 20 * 60;

    Map<String, Image> imageCache = new HashMap<>();

    @FXML
    void initialize() {
        currentMode = TestModeContext.getInstance().getMode();

        optionsGroup = new ToggleGroup();
        loadQuestions();
        updateQuestionTiles();
        startTimer();
        loadQuestion(currentQuestionIndex);
    }

    private void loadQuestions() {
        if (TestModeContext.getInstance().getMode() == TestModeContext.Mode.TOPIC) {
            questions = TestModeContext.getInstance().getQuestions();
            return;
        }

        List<Question> allQuestions = QuestionCache.getQuestions();
        Collections.shuffle(allQuestions);
        System.out.println("📥 [EXAM] Завантажено з кешу");

        if (currentMode == TestModeContext.Mode.EXAM || currentMode == TestModeContext.Mode.PRACTICE) {
            questions = allQuestions.subList(0, Math.min(20, allQuestions.size()));
        }
    }

    private void updateQuestionTiles() {
        questionTiles.getChildren().clear();

        for (int i = 0; i < questions.size(); i++) {
            final int index = i;
            Button tile = new Button(String.valueOf(i + 1));
            tile.setMinSize(40, 40);
            tile.setPrefSize(40, 40);
            tile.setMaxSize(40, 40);
            tile.setWrapText(false);
            tile.setWrapText(true);
            tile.setPrefWidth(40);
            tile.getStyleClass().add("question-tile");

            if (i == currentQuestionIndex) {
                tile.getStyleClass().add("active-tile");
            } else if (answeredIncorrectly.contains(i)) {
                tile.getStyleClass().add("wrong-tile");
            } else if (answeredCorrectly.contains(i)) {
                tile.getStyleClass().add("correct-tile");
            }

            tile.setOnAction(e -> {
                currentQuestionIndex = index;
                loadQuestion(currentQuestionIndex);
            });

            questionTiles.getChildren().add(tile);
        }
    }

    private void highlightActiveTile(int index) {
        for (int i = 0; i < questionTiles.getChildren().size(); i++) {
            Button tile = (Button) questionTiles.getChildren().get(i);
            tile.getStyleClass().removeAll("active-tile", "correct-tile", "wrong-tile");

            if (i == index) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), tile);
                ft.setFromValue(0.5);
                ft.setToValue(1.0);
                ft.play();

                tile.getStyleClass().add("active-tile");

            } else if (answeredIncorrectly.contains(i)) {
                tile.getStyleClass().add("wrong-tile");
            } else if (answeredCorrectly.contains(i)) {
                tile.getStyleClass().add("correct-tile");
            }
        }
    }


    private void loadQuestion(int index) {
        if (index < questions.size()) {
            Question q = questions.get(index);
            questionLabel.setText((index + 1) + ". " + q.getQuestionText());

            optionsBox.getChildren().clear();
            optionsGroup.getToggles().clear();

            int correctIndex = q.getCorrectAnswerIndex();
            Integer selected = selectedAnswers.get(index);

            for (int i = 0; i < q.getOptions().size(); i++) {
                ToggleButton tb = new ToggleButton(q.getOptions().get(i));
                tb.setWrapText(true);
                tb.setUserData(i);
                tb.setToggleGroup(optionsGroup);
                tb.getStyleClass().add("answer-button");

                if (selected != null) {
                    tb.setSelected(i == selected);

                    if (answeredIncorrectly.contains(index)) {
                        if (i == selected && selected != correctIndex) {
                            tb.setStyle("-fx-background-color: #ffcccc;"); // червона — неправильна
                        }
                        if (i == correctIndex) {
                            tb.setStyle("-fx-background-color: #ccffcc;"); // зелена — правильна
                        }
                    } else if (answeredCorrectly.contains(index) && i == selected) {
                        tb.setStyle("-fx-background-color: #ccffcc;"); // зелена — правильна
                    }
                }

                optionsBox.getChildren().add(tb);
            }

            if (q.getImg() != null && !q.getImg().isEmpty()) {
                String imageUrl = "https://ggxbivxwcesrnhfagrti.supabase.co/storage/v1/object/public/questions-images/" + q.getImg();
                if (!imageCache.containsKey(imageUrl)) {
                    javafx.scene.image.Image img = new javafx.scene.image.Image(imageUrl, true);
                    imageCache.put(imageUrl, img);
                }
                questionImage.setImage(imageCache.get(imageUrl));
            } else {
                questionImage.setImage(null);
            }

            highlightActiveTile(index);
        } else {
            finishTest();
        }
    }


    @FXML
    void nextQuestion() {
        if (optionsGroup.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Увага!");
            alert.setHeaderText(null);
            alert.setContentText("Оберіть варіант відповіді!");
            alert.showAndWait();
            return;
        }

        checkAnswer();
    }

    private void checkAnswer() {
        int selectedAnswer = (int) optionsGroup.getSelectedToggle().getUserData();
        Question currentQuestion = questions.get(currentQuestionIndex);

        selectedAnswers.put(currentQuestionIndex, selectedAnswer);

        if (currentQuestion.isCorrectAnswer(selectedAnswer)) {
            correctAnswers++;
            answeredCorrectly.add(currentQuestionIndex);
        } else {
            mistakeIds.add(currentQuestion.getId());
            answeredIncorrectly.add(currentQuestionIndex);
            mistakeCount++;

            if (currentMode == TestModeContext.Mode.EXAM && mistakeCount > MAX_EXAM_MISTAKES) {
                showFailure();
                return;
            }
        }

        for (int i = 0; i < questions.size(); i++) {
            if (!answeredCorrectly.contains(i) && !answeredIncorrectly.contains(i)) {
                currentQuestionIndex = i;
                updateQuestionTiles();
                loadQuestion(currentQuestionIndex);
                return;
            }
        }

        finishTest();
    }


    private void startTimer() {
        timerLabel.setText("Час: 0 сек");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
            timeSeconds++;
            updateTimerDisplay();

            if (currentMode == TestModeContext.Mode.EXAM && timeSeconds >= MAX_EXAM_TIME_SECONDS) {
                showFailure();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerDisplay() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("Час: %02d:%02d", minutes, seconds));
    }

    private void showFailure() {
        timeline.stop();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/result-view.fxml"));
            Parent view = loader.load();

            ResultController controller = loader.getController();
            ResultModel result = new ResultModel(correctAnswers, questions.size(), timeSeconds);
            result.setFailed(true);
            controller.setResult(result);

            LayoutController.getInstance().setContent(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void finishTest() {
        timeline.stop();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/result-view.fxml"));
            Parent view = loader.load();

            ResultController controller = loader.getController();
            controller.setResult(new ResultModel(correctAnswers, questions.size(), timeSeconds));

            LayoutController.getInstance().setContent(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void returnToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home-view.fxml"));
            Parent view = loader.load();
            LayoutController.getInstance().setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
