package com.coursehelper.frontend;

import java.io.IOException;

import com.coursehelper.frontend.theme.ThemeManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Stage primaryStage;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        ThemeManager.initializeGuest();

        Parent accessRoot = loadFXML("accessScreen");
        ThemeManager.setAccessScreenTheme(accessRoot, ThemeManager.getCurrentTheme());

        scene = new Scene(accessRoot, 800, 555);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    
    }


    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/FXML/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}