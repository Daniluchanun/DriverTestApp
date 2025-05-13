package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.AuthContext;
import services.AuthService;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            statusLabel.setText("Усі поля обов'язкові");
            return;
        }

        try {
            String response = authService.login(email, password);
            System.out.println("Login success: " + response);

            AuthContext.getInstance().setSession(
                    authService.getUserId(),
                    email,
                    authService.getAccessToken()
            );

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Помилка входу: " + e.getMessage());
        }
    }


    @FXML
    void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            statusLabel.setText("Усі поля обов'язкові");
            return;
        }

        try {
            String response = authService.register(email, password);
            System.out.println("Register success: " + response);
            statusLabel.setText("✅ Реєстрація успішна!");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Помилка реєстрації: " + e.getMessage());
        }
    }
}
