package com.saferoom.gui;

import com.saferoom.email.*;
import com.saferoom.crypto.VerificationCodeGenerator;
import com.saferoom.db.DBManager;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterMenu {
	
	private static final String ICON_PATH = "src/resources/Verificate.png";
	static String Subject = "Verify Your Account!";
	static String Body = "Hello,I believe this is belong to you! -->";
	static String verCode = VerificationCodeGenerator.generateVerificationCode();
	static String fullCode = Body + verCode;
	


    public static VBox createRegisterMenu() {
        VBox menu = new VBox(15);
        menu.setTranslateX(250);
        menu.setTranslateY(80);

        menu.setRotationAxis(javafx.scene.transform.Rotate.Y_AXIS);
        menu.setRotate(-8);        
        menu.setTranslateZ(-15);  

        // BoxBlur(radiusX, radiusY, iterations)
        BoxBlur blur = new BoxBlur(1, 1, 5);
        menu.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.12);" + 
            "-fx-background-radius: 15;" +
            "-fx-padding: 30;" +
            "-fx-border-color: rgba(255,255,255,0.2);" +         
            "-fx-border-radius: 15;"
        );
        menu.setEffect(blur);

        Label titleLabel = new Label("Register");
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        titleLabel.setFont(new Font("Arial", 26));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setStyle(
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #dddddd;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;"
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle(
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #dddddd;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;"
        );

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle(
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #dddddd;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #dddddd;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;"
        );

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle(
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #dddddd;" +
            "-fx-background-color: transparent;" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8;"
        );

                Button registerButton = new Button("Register");
        registerButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #66ccff, #0080ff);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 16;"
        );
        registerButton.setFont(new Font("Arial", 14));

        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if(name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
            	System.out.println("Please fill the all blocks");
            	return ;
            }
            
            if(!(email.contains("@")  && ( email.contains("gmail") ||email.contains("hotmail"))  && email.contains("com") && email.contains(".")))
            {
            	System.out.println("Unvalid emial try again");
            	return ;
            }
            if(password.length() < 8) {
            	System.out.println("Your password should at least 8 character");
            	return ;
            }
            	
            if (!password.equals(confirmPassword)) {
                System.out.println("Password and Confirm Password do not match!");
                return ;
            }
            
           if(!(password.matches(".*[A-Z].*") && password.matches(".*\\d.*") && password.matches(".*[^a-zA-Z0-9].*")))
           {
        	   System.out.println("Password must contains at least one uppercase, one number, one symbolic character for security");
        	   return ;
           }
           
           

            System.out.println("Registering: " + name + ", " + username + ", " + email);
            try {
				DBManager.createUser(username, confirmPassword, email);
				DBManager.setVerificationCode(username, verCode);
				
				if(EmailSender.sendEmail(email, Subject, fullCode, ICON_PATH)) {
					
					System.out.println("Hesabı doğrulamak için emailinize gönderdiğimiz kodu girin:");
					
					menu.getChildren().clear();
					VantaEffectFXMouse.mode = 1;
				    VerificationMenu.createVerificationMenu(username, menu);
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
        });

        menu.getChildren().addAll(
            titleLabel,
            nameField,
            usernameField,
            emailField,
            passwordField,
            confirmPasswordField,
            registerButton
        );
        return menu;
    }
    
   
    
    
    
}
