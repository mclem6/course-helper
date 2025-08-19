package com.coursehelper;

import java.io.IOException;

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
        //load access screen
        primaryStage = stage;
        scene = new Scene(loadFXML("accessScreen"), 800, 555);
        scene.getStylesheets().add(getClass().getResource("/stylesheets/DarkMode/accessPageDark.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    
    }

    //sets the scene, pass root result of loadFMXL which returns root
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/FXML/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}