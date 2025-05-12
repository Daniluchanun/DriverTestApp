package models;

public class ResultModel {
    private int correct;
    private int total;
    private int timeSeconds;

    public ResultModel(int correct, int total, int timeSeconds) {
        this.correct = correct;
        this.total = total;
        this.timeSeconds = timeSeconds;
    }

    public int getCorrect() {
        return correct;
    }

    public int getTotal() {
        return total;
    }

    private boolean failed = false;

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }


    public String getFormattedTime() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
