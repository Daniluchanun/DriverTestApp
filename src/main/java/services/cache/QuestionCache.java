package services;

import models.Question;
import java.util.List;

public class QuestionCache {
    private static List<Question> cachedQuestions;

    public static void setQuestions(List<Question> questions) {
        cachedQuestions = questions;
    }

    public static List<Question> getQuestions() {
        return cachedQuestions;
    }

    public static boolean isLoaded() {
        return cachedQuestions != null && !cachedQuestions.isEmpty();
    }
}
