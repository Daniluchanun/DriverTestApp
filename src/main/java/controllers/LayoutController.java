package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.ScaleTransition;


public class LayoutController {

    @FXML
    private ImageView logoImage;

    @FXML
    private StackPane contentPane;

    @FXML
    private VBox sidebar;

    @FXML
    private Button homeButton;

    @FXML
    private Button topicsButton;

    @FXML
    private Button mistakesButton;

    @FXML
    private Button profileButton;

    private FontIcon profileIcon;


    private FontIcon homeIcon;
    private FontIcon topicsIcon;
    private FontIcon mistakesIcon;

    private final double COLLAPSED_WIDTH = 60;
    private final double EXPANDED_WIDTH = 180;

    private static LayoutController instance;

    public void initialize() {
        instance = this;
        setupSidebarAnimation();
        setupIcons();
        loadView("/views/home-view.fxml");
        homeButton.getStyleClass().add("active");
        logoImage.setImage(new Image(getClass().getResource("/public/logo.png").toExternalForm()));

        homeButton.setText("");
        topicsButton.setText("");
        mistakesButton.setText("");
        profileButton.setText("");

        homeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        topicsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        mistakesButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        profileButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        homeButton.setCursor(Cursor.HAND);
        topicsButton.setCursor(Cursor.HAND);
        mistakesButton.setCursor(Cursor.HAND);
        profileButton.setCursor(Cursor.HAND);

        homeButton.getStyleClass().add("sidebar-button");
        topicsButton.getStyleClass().add("sidebar-button");
        mistakesButton.getStyleClass().add("sidebar-button");
        profileButton.getStyleClass().add("sidebar-button");
    }


    private void setupSidebarAnimation() {
        sidebar.setPrefWidth(COLLAPSED_WIDTH);
        sidebar.setOnMouseEntered((MouseEvent e) -> animateSidebar(EXPANDED_WIDTH, true));
        sidebar.setOnMouseExited((MouseEvent e) -> animateSidebar(COLLAPSED_WIDTH, false));
    }

    private void animateSidebar(double targetWidth, boolean expand) {
        Timeline sidebarResize = new Timeline();
        KeyValue kv = new KeyValue(sidebar.prefWidthProperty(), targetWidth);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        sidebarResize.getKeyFrames().add(kf);
        sidebarResize.play();

        TranslateTransition ttHome = new TranslateTransition(Duration.millis(200), homeIcon);
        TranslateTransition ttTopics = new TranslateTransition(Duration.millis(200), topicsIcon);
        TranslateTransition ttMistakes = new TranslateTransition(Duration.millis(200), mistakesIcon);
        TranslateTransition ttProfile = new TranslateTransition(Duration.millis(200), profileIcon);

        ScaleTransition logoScale = new ScaleTransition(Duration.millis(200), logoImage);

        if (expand) {
            logoScale.setToX(2.3);
            logoScale.setToY(2.3);

            ttHome.setToX(-1);
            ttTopics.setToX(-1);
            ttMistakes.setToX(-1);
            ttProfile.setToX(-1);

            homeButton.setText("Головна");
            topicsButton.setText("Теми");
            mistakesButton.setText("Помилки");
            profileButton.setText("Профіль");

            animateButtonWidth(homeButton, 160);
            animateButtonWidth(topicsButton, 160);
            animateButtonWidth(mistakesButton, 160);
            animateButtonWidth(profileButton, 160);

            homeButton.setContentDisplay(ContentDisplay.LEFT);
            topicsButton.setContentDisplay(ContentDisplay.LEFT);
            mistakesButton.setContentDisplay(ContentDisplay.LEFT);
            profileButton.setContentDisplay(ContentDisplay.LEFT);

        } else {
            logoScale.setToX(1.2);
            logoScale.setToY(1.2);

            ttHome.setToX(0);
            ttTopics.setToX(0);
            ttMistakes.setToX(0);
            ttProfile.setToX(0);

            animateButtonWidth(homeButton, 60);
            animateButtonWidth(topicsButton, 60);
            animateButtonWidth(mistakesButton, 60);
            animateButtonWidth(profileButton, 60);

            homeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            topicsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            mistakesButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            profileButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            homeButton.setText("");
            topicsButton.setText("");
            mistakesButton.setText("");
            profileButton.setText("");
        }

        logoScale.play();
        ttHome.play();
        ttTopics.play();
        ttMistakes.play();
        ttProfile.play();
    }


    private void animateButtonWidth(Button button, double targetWidth) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(button.prefWidthProperty(), targetWidth);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }


    private void setupIcons() {
        homeIcon = new FontIcon("fas-car");
        homeIcon.setIconSize(18);
        homeIcon.setIconColor(Color.WHITE);
        homeButton.setGraphic(homeIcon);

        topicsIcon = new FontIcon("fas-book");
        topicsIcon.setIconSize(18);
        topicsIcon.setIconColor(Color.WHITE);
        topicsButton.setGraphic(topicsIcon);

        mistakesIcon = new FontIcon("fas-times-circle");
        mistakesIcon.setIconSize(18);
        mistakesIcon.setIconColor(Color.WHITE);
        mistakesButton.setGraphic(mistakesIcon);

        profileIcon = new FontIcon("fas-user");
        profileIcon.setIconSize(18);
        profileIcon.setIconColor(Color.WHITE);
        profileButton.setGraphic(profileIcon);
    }

    private void clearActive() {
        homeButton.getStyleClass().remove("active");
        topicsButton.getStyleClass().remove("active");
        mistakesButton.getStyleClass().remove("active");
        profileButton.getStyleClass().remove("active");
    }

    @FXML
    void goHome() {
        clearActive();
        homeButton.getStyleClass().add("active");
        loadView("/views/home-view.fxml");
    }

    @FXML
    void goTopics() {
        clearActive();
        topicsButton.getStyleClass().add("active");
        loadView("/views/topics-view.fxml");
    }

    @FXML
    void goMistakes() {
        clearActive();
        mistakesButton.getStyleClass().add("active");
        loadView("/views/my-mistakes-view.fxml");
    }

    @FXML
    void goProfile() {
        clearActive();
        profileButton.getStyleClass().add("active");
        loadView("/views/profile-view.fxml");
    }

    public static LayoutController getInstance() {
        return instance;
    }

    public void setContent(Parent node) {
        contentPane.getChildren().setAll(node);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
