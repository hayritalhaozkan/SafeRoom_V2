package com.saferoom.gui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 *  Yalnızca login/register görünümünü üretir.
 *  Başarılı girişte loginSuccess.accept(username) çağrılır.
 */
public class VantaLoginPane {

    public static VBox build(Consumer<String> loginSuccess) {

        VBox pane = new VBox(14);
        pane.setPadding(new Insets(30));
        pane.setAlignment(Pos.CENTER);
        pane.setEffect(new BoxBlur(10,10,3));
        pane.setStyle("""
                -fx-background-color: rgba(255,255,255,0.15);
                -fx-background-radius: 18;
                """);

        Label title = new Label("SafeRoom – Sign in");
        title.setFont(Font.font("Inter", 26));
        title.setTextFill(Color.WHITE);

        TextField user = new TextField();
        user.setPromptText("username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("password");

        Button login = new Button("Login");
        login.setMaxWidth(Double.MAX_VALUE);

        login.setOnAction(e -> {
            String u = user.getText().trim();
            String p = pass.getText().trim();
            // ‼️ Burada DB kontrolü veya stub:
            if (u.isEmpty() || p.isEmpty()) return;
            // OK → callback
            loginSuccess.accept(u);
            // hafif fading
            FadeTransition ft = new FadeTransition(Duration.millis(300), pane);
            ft.setToValue(0); ft.play();
        });

        pane.getChildren().addAll(title, user, pass, login);
        return pane;
    }
}
