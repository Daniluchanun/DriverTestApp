package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    private int id;

    @JsonProperty("question_text")
    private String questionText;

    private List<String> options;

    @JsonProperty("correct_answer_index")
    private int correctAnswerIndex;

    private String theme;

    @JsonProperty("image_path")
    private String img;

    public Question() {
    }

    public Question(int id, String questionText, List<String> options, int correctAnswerIndex, String theme, String img) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.theme = theme;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isCorrectAnswer(int chosenAnswerIndex) {
        return chosenAnswerIndex == correctAnswerIndex;
    }
}
