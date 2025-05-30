package com.coursehelper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private Stage stage;

    public SceneManager(Stage stage){
        this.stage = stage;
    }

    public void switchScene(String fxmlPath){
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
