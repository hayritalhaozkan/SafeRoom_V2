package com.saferoom.gui;


import java.sql.SQLException;

import com.saferoom.client.ClientMenu;
import com.saferoom.db.DBManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class VerificationMenu {

    public static void createVerificationMenu(String username, VBox menu) {
        menu.getChildren().clear(); // Clear the old items
        menu.setAlignment(Pos.CENTER); 

        BoxBlur blur = new BoxBlur(1, 1, 0);
        menu.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);" + 
                      "-fx-background-radius: 15;" +
                      "-fx-padding: 30;" +
                      "-fx-border-color: rgba(0,191,255,0.5);" + 
                      "-fx-border-radius: 15;" +
                      "-fx-backdrop-filter: blur(10px);"); 

        menu.setEffect(blur);

        Label title = new Label("Enter the Verification Code");
        title.setFont(new Font("Arial", 26));
        title.setTextFill(Color.web("#00bfff"));

        TextField codeField = new TextField();
        codeField.setPromptText("Verification Code");
        codeField.setStyle("-fx-text-fill: white;" +
                           "-fx-prompt-text-fill: #aaaaaa;" +
                           "-fx-background-color: rgba(0,0,0,0.3);" +
                           "-fx-border-color: #00bfff;" +
                           "-fx-border-radius: 5;" +
                           "-fx-background-radius: 5;" +
                           "-fx-padding: 8;");

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #00bfff;");

        Label timerLabel = new Label("60");
        timerLabel.setFont(new Font("Arial", 20));
        timerLabel.setTextFill(Color.web("#00bfff"));

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: linear-gradient(to right, #00bfff, #0080ff);" +
                              "-fx-text-fill: white;" +
                              "-fx-font-weight: bold;" +
                              "-fx-background-radius: 8;" +
                              "-fx-cursor: hand;" +
                              "-fx-padding: 8 16;");
        submitButton.setFont(new Font("Arial", 14));

        final int totalTime = 60;
        final int[] remainingTime = { totalTime };
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime[0]--;
            timerLabel.setText(String.valueOf(remainingTime[0]));
            progressBar.setProgress((totalTime - remainingTime[0]) / (double) totalTime);
            if (remainingTime[0] <= 0) {
                submitButton.setDisable(true);
            }
        }));
        timeline.setCycleCount(totalTime);
        timeline.play();

        Timeline heartbeat = new Timeline(
            new KeyFrame(Duration.millis(500), e -> {
                menu.setScaleX(1.02);
                menu.setScaleY(1.02);
            }),
            new KeyFrame(Duration.millis(1000), e -> {
                menu.setScaleX(1.0);
                menu.setScaleY(1.0);
            })
        );
        heartbeat.setCycleCount(Timeline.INDEFINITE);
        heartbeat.play();

        submitButton.setOnAction(e -> {
            timeline.stop();
            String enteredCode = codeField.getText().trim();

            if (enteredCode.isEmpty()) {
                System.out.println("Please enter the code!!");
                return;
            }
            if (enteredCode.length() != 8) {
                System.out.println("Wrong length!");
                return;
            }

            int client_api_code = ClientMenu.verify_user(username, enteredCode);
			if (client_api_code == 0) {
			    System.out.println("Congrats!! You are verified now!!");

			    Platform.runLater(() -> {
			        try {
			        	menu.getChildren().clear();
						DBManager.Verify(username);
						VantaEffectFXMouse.mode = 2;
						Thread.sleep(0,3);
						VantaEffectFXMouse.mode = 3;
						Thread.sleep(0,2);
						StarEffect2.explodeStarEffect(VantaEffectFXMouse.points);
						

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    });

			} else {
			    System.out.println("Incorrect Code!");
			}
        });

        menu.getChildren().addAll(title, codeField, progressBar, timerLabel, submitButton);
    }
}
