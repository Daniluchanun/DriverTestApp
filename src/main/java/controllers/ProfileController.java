package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {

    @FXML
    private Label dummyLabel;

    public void initialize() {
        System.out.println("Сторінка профілю завантажена");
    }
}
