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

    public void switchScene(String fxmlPath, String styleSheetPath){
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();

            //set style sheet of homepage
            root.getStylesheets().add(getClass().getResource(styleSheetPath).toExternalForm());

            //use prev scene
            Scene currentScene = stage.getScene();

            //check if it exists
            if(stage.getScene() != null){
                //it exists,just replace
                currentScene.setRoot(root);
            } else {
                //doesnt exist create new one
                currentScene = new Scene(root);
                stage.setScene(currentScene);
            }
        
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
