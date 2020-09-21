package com.github.jscancella;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application{
  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(final Stage stage) throws Exception{
    final URL fxmlUrl = getClass().getClassLoader().getResource("dedupUI.fxml");
    
    final Parent root = FXMLLoader.load(fxmlUrl);
    final Scene scene = new Scene(root);
    
    stage.setTitle("Deplicate Files");
    stage.setScene(scene);
    stage.show();
  }
}
