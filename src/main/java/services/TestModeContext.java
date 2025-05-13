package services;


import models.Question;

import java.util.List;

public class TestModeContext {
    public enum Mode {
        EXAM,
        PRACTICE,
        MISTAKES,
        FAVORITES,
        FREQUENT,
        TOPIC
    }

    private static TestModeContext instance;
    private Mode mode = Mode.EXAM;
    private List<Question> customQuestions;
    private TestModeContext() {}

    public static TestModeContext getInstance() {
        if (instance == null) {
            instance = new TestModeContext();
        }
        return instance;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public void setQuestions(List<Question> questions) {
        this.customQuestions = questions;
    }

    public List<Question> getQuestions() {
        return customQuestions;
    }
}
