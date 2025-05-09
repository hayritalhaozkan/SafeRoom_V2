// SafeRoomApp.java
package com.saferoom.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SafeRoomApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. Vanta animasyon + login menüsü (arka plan)
        VantaEffectFXMouse vanta = new VantaEffectFXMouse();
        vanta.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
